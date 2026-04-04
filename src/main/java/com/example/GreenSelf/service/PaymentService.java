package com.example.GreenSelf.service;

import com.example.GreenSelf.Dto.PaymentVerifyRequest;
import com.example.GreenSelf.entity.Order;
import com.example.GreenSelf.entity.OrderStatus;
import com.example.GreenSelf.entity.Payment;
import com.example.GreenSelf.repo.OrderRepo;
import com.example.GreenSelf.repo.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired private PaymentRepo paymentRepo;
    @Autowired private OrderRepo orderRepo;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    // ── Verify Payment After Checkout ─────────────────────────────────────────
    @Transactional
    public boolean verifyPayment(PaymentVerifyRequest req) {
        try {
            System.out.println("===== VERIFY PAYMENT START =====");

            // 🔹 Step 1: Incoming request
            System.out.println("OrderId: " + req.getRazorpayOrderId());
            System.out.println("PaymentId: " + req.getRazorpayPaymentId());
            System.out.println("Signature: " + req.getRazorpaySignature());

            // 🔹 Step 2: Generate HMAC
            String payload = req.getRazorpayOrderId() + "|" + req.getRazorpayPaymentId();
            System.out.println("Payload: " + payload);

            String generated = generateHmac(payload, keySecret);
            System.out.println("Generated Signature: " + generated);

            // 🔹 Step 3: Compare signatures
            if (!generated.equals(req.getRazorpaySignature())) {
                System.out.println("❌ Signature mismatch");
                markFailed(req.getRazorpayOrderId(), "Signature mismatch — possible fraud");
                return false;
            }

            System.out.println("✅ Signature verified");

            // 🔹 Step 4: Fetch Order
            Order order = orderRepo.findByRazorpayOrderId(req.getRazorpayOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            System.out.println("Order found: " + order.getId());

            // 🔹 Step 5: Update Order
            order.setRazorpayPaymentId(req.getRazorpayPaymentId());
            order.setRazorpaySignature(req.getRazorpaySignature());
            order.setOrderStatus(OrderStatus.PAID);
            order.setPaidAt(LocalDateTime.now());
            orderRepo.save(order);

            System.out.println("✅ Order updated to PAID");

            // 🔹 Step 6: Fetch/Create Payment
            Payment payment = paymentRepo
                    .findByRazorpayOrderId(req.getRazorpayOrderId())
                    .orElse(new Payment());

            System.out.println("Payment object: " + payment);

            // 🔹 Step 7: Update Payment
            payment.setRazorpayPaymentId(req.getRazorpayPaymentId());
            payment.setRazorpaySignature(req.getRazorpaySignature());
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            paymentRepo.save(payment);

            System.out.println("✅ Payment saved SUCCESS");

            System.out.println("===== VERIFY PAYMENT END =====");

            return true;

        } catch (Exception e) {
            System.out.println("❌ ERROR in verifyPayment: " + e.getMessage());
            e.printStackTrace();  // 🔥 VERY IMPORTANT

            markFailed(req.getRazorpayOrderId(), e.getMessage());
            return false;
        }
    }
    // ── Mark order + payment as failed ────────────────────────────────────────
    private void markFailed(String razorpayOrderId, String reason) {
        orderRepo.findByRazorpayOrderId(razorpayOrderId).ifPresent(order -> {
            order.setOrderStatus(OrderStatus.FAILED);
            orderRepo.save(order);
        });

        paymentRepo.findByRazorpayOrderId(razorpayOrderId).ifPresent(payment -> {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason(reason);
            paymentRepo.save(payment);
        });
    }

    // ── HMAC SHA256 ───────────────────────────────────────────────────────────
    private String generateHmac(String data, String secret) throws Exception {
        System.out.println("Generating HMAC...");
        System.out.println("Secret used: " + secret);

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
        byte[] hash = mac.doFinal(data.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));

        return sb.toString();
    }
}