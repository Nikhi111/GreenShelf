# GreenSelf Frontend Architecture & Planning Document

## 🧱 1. Project Overview
* **Brief Description:** GreenSelf is a comprehensive e-commerce platform that connects users with plant nurseries. Users can browse an extensive catalog of plants and nursery products, manage a shopping cart, place orders, and make payments. Sellers can manage multiple nurseries, oversee inventory, and analyze product performance. Admins oversee the ecosystem by managing nurseries and platform users.
* **Key Features Derived from APIs:** 
  * User authentication, registration, and profile management.
  * Comprehensive shopping cart and order processing pipeline.
  * Integration with Razorpay for handling payments and webhooks.
  * Granular seller dashboard to track nursery inventory and sales performance.
  * Dedicated Admin suite to search, list, and verify sellers and nurseries.
  * Robust product and plant catalog with search and categorization.

---

## 🏗️ 2. Frontend Architecture
* **Folder Structure:**
  ```text
  /src
  ├── /assets       # Static files (images, icons, fonts)
  ├── /components   # Reusable UI components
  │   ├── /common   # Buttons, inputs, modals, loaders
  │   ├── /layout   # Navbar, Footer, Sidebar
  │   └── /features # Domain-specific components (e.g., ProductCard)
  ├── /config       # Environment variables and constants
  ├── /hooks        # Custom React hooks (useAuth, useCart, useDebounce)
  ├── /pages        # Route-level components (Login, Dashboard, ProductList)
  ├── /services     # API module calls and Axios configuration
  ├── /store        # Zustand state slices (authStore, cartStore)
  ├── /styles       # Tailwind global CSS, Framer Motion variants
  └── /utils        # Helper formatters (dates, currency, error parsers)
  ```
* **Explanation of Folders:** The structure follows a feature-by-layer convention. `/pages` map to React Router routes, `/components` are shared building blocks, `/services` abstracts API interactions, and `/store` isolates global state management logic.
* **Tech Stack:**
  * **React:** Core UI library for building the SPA (Single Page Application).
  * **Axios:** For robust HTTP requests, handling interceptors for auth tokens.
  * **Zustand:** Lightweight and scalable global state management.
  * **React Router:** For client-side routing and protected route layers.
  * **Tailwind CSS:** Utility-first CSS framework for rapid and consistent styling.
  * **Framer Motion:** For fluid micro-interactions and page transition animations.

---

## 🔌 3. API Integration Plan

* **Auth & User Module**
  * `POST /api/auth/login` → `loginUser(credentials)`
  * `POST /api/user/register` → `registerUser(details)`
  * `GET /api/user/me` → `getUserProfile()`

* **Products & Plants Module**
  * `GET /api/products/public` → `getPublicProducts()`
  * `GET /api/products/search?q=` → `searchProducts(query)`
  * `GET /api/products/{productId}` → `getProductDetails(id)`
  * `GET /api/products/category/{category}` → `getProductsByCategory(category)`
  * `GET /api/plants`, `GET /api/plants/search` → `getPlants()`, `searchPlants(query)`

* **Cart & Orders Module**
  * `GET /api/user/cart` → `fetchCart()`
  * `POST /api/user/cart` → `addToCart(itemPayload)`
  * `DELETE /api/user/cart/{itemId}` → `removeFromCart(itemId)`
  * `POST /api/order/create` → `createOrder(orderData)`
  * `GET /api/user/orders` → `getUserOrders()`
  * `GET /api/user/orders/{orderId}` → `getOrderDetails(orderId)`

* **Seller & Nursery Module**
  * `POST /api/seller/register` → `registerSeller()`
  * `GET /api/seller/nursery` → `getSellerNurseries()`
  * `POST /api/seller/nursery/{nurseryId}/product` → `addProductToNursery(id, product)`
  * `GET /api/seller/nursery/{nurseryId}/inventory` → `getNurseryInventory(id)`
  * `GET /api/seller/nursery/{nurseryId}/dashboard/product-performance` → `getPerformanceMetrics(id)`
  * `DELETE /api/seller/nursery/{nurseryId}/product/{productId}` → `deleteProduct(nurseryId, productId)`

* **Payment & Admin Modules**
  * `POST /api/payment/verify` → `verifyPayment(details)`
  * `GET /api/payment/status/{orderId}` → `getPaymentStatus(orderId)`
  * `GET /api/admin/nurseries`, `GET /api/admin/sellers` → `getAllNurseries()`, `getAllSellers()`
  * `GET /api/admin/search-seller`, `GET /api/admin/search-nursery` → `searchSellers(query)`, `searchNurseries(query)`

---

## 🔐 4. Authentication Flow
* **Login / Signup Flow:** The user submits the form; Axios sends a request to the backend. On successful login, the API returns a JWT token and user profile details.
* **JWT Handling:** An Axios request interceptor attaches the JWT token (`Authorization: Bearer <token>`) to every subsequent API call automatically.
* **Token Storage Strategy:** The token will be stored securely in `localStorage` for persistence (or `sessionStorage` based on "Remember Me" toggle), and mirrored into the Zustand `authStore`.
* **Protected Routes:** A `<ProtectedRoute>` wrapper component will wrap protected pages. It will check the Zustand store for authentication status and required roles (e.g., User, Admin, Seller), redirecting unauthorized access to `/login`.

---

## 🧠 5. State Management Plan
* **Global State (Zustand):**
  * `useAuthStore`: Holds `user` object, `token`, `isAuthenticated`, and `role`.
  * `useCartStore`: Holds `cartItems`, `cartTotal`, and background syncing status.
  * `useUIStore`: Manages global UI elements like `isSidebarOpen`, active modals, and theme settings.
* **Local State (React useState/useReducer):**
  * Form inputs, active tab selection, and localized component loading states will remain local to avoid unnecessary global re-renders.
* **Store Structure Design:** Utilizing Zustand's sliced architecture, keeping domains strictly separated (no monolithic stores).

---

## 📄 6. In-Depth Pages & Routing Plan

This section breaks down each route, the layout wrapper, core responsibilities, and nested components.

* **`/login` & `/signup` — Authentication Pages**
  * *Purpose:* Secure entry points for generic users and new sellers (`/api/seller/register`).
  * *Layout Wrapper:* `AuthLayout` (minimalist design without standard navbar).
  * *Sub-components:* 
    * `AuthBanner`: A visual left-panel illustration about sustainability/plants.
    * `LoginForm` / `RegisterForm`: Include `Input` components with native validations.
    * `RoleToggle`: A pill switch to switch between registering as User vs Nursery Seller.

* **`/` or `/home` — Landing Page**
  * *Purpose:* Welcome users, highlight trending products, and display nursery featured banners.
  * *APIs used:* `/api/products/public` (with sorting/limit params if available).
  * *Sub-components:*
    * `HeroSection`: Framer motion animated banners showcasing special offers (e.g. spring sales).
    * `TrendingGrid`: A grid mapping 4-8 top-tier `ProductCard` components.
    * `CategoryCarousel`: Circular `Card`s representing different plant categories.

* **`/products` — Discovery / Product Listing**
  * *Purpose:* Advanced searching, filtering, and browsing of the platform's catalog.
  * *APIs used:* `/api/products/search`, `/api/products/category/{category}`.
  * *Sub-components:*
    * `FilterSidebar`: Accordions holding checkboxes for constraints (Categories, Stock).
    * `SortDropdown`: To toggle between "Price: Low to High", "Newest", etc.
    * `ProductGrid`: Responsive CSS grid housing mappings of `ProductCard`.
    * `EmptyStateBanner`: Shown beautifully if search queries return 0 results.

* **`/products/:productId` — Product Details Page (PDP)**
  * *Purpose:* Show comprehensive plant attributes, seller info, and Add to Cart mechanism.
  * *APIs used:* `/api/products/details/{productId}`
  * *Sub-components:*
    * `ProductImageGallery`: Main spotlight image and smaller selectable thumbnails below.
    * `ProductMeta`: Title, Price, detailed descriptions, badge tags (Eco-friendly, Rare).
    * `QuantitySelector`: Controlled Input modifier with +/- buttons to restrict max ordering limits.
    * `AddToCartButton`: Dispatches payload immediately via Axios to `/api/user/cart`.
    * `SellerBadge`: Small layout component surfacing the Nursery that manages this stock.

* **`/cart` & `/checkout` — Cart / Orders Hub**
  * *Purpose:* Review intent to purchase, confirm pricing/quantities, map Razorpay gateway.
  * *APIs used:* `/api/user/cart`, `/api/order/create`, `/api/payment/verify`.
  * *Sub-components:*
    * `CartItemList`: Vertical stack of `CartItemLine` (thumb, title, quantity controls, delete icon).
    * `OrderSummaryPanel`: Breaks down Subtotal, Taxes, Shipping, and calculated Grand Total.
    * `CheckoutForm`: Final prompt collecting shipping and billing verification.
    * `PaymentGatewayWrapper`: Instantiates the Razorpay script and window pop-up handler.

* **`/dashboard/*` — Role-based Internal Portals**
  * *Routing Strategy:* Nested sub-routes depending on role (e.g. `/dashboard/admin` vs `/dashboard/seller`).
  * *User Hub (`/dashboard/user`):*
    * `OrderHistoryList`: Table view mapping historical purchases.
    * `ProfileSettingsForm`: Simple profile mutation.
  * *Seller Hub (`/dashboard/seller`):*
    * `InventoryTable`: CRUD complex data-table view wrapping `/api/seller/nursery/{id}/inventory`.
    * `AddProductModal`: Multi-step form for creating plants (Name, Price Category).
    * `PerformanceMetrics`: Visual KPI `Card` components for metrics like sales or top plants.
  * *Admin Hub (`/dashboard/admin`):*
    * `NurseryApprovalList`: Tool to vet and verify platform seller applications.
    * `UserManagementTable`: Overlook all platform members querying `/api/admin/sellers`.

---

## 🧩 7. Detailed Component API & Design Plan

This expands on the precise architectural properties (Props) and state management of crucial components.

### 🧱 Reusable UI Atoms & Molecules (`/src/components/common`)
* **`Button.jsx`**
  * *Props:* `variant` (`primary` | `secondary` | `danger` | `ghost`), `size` (`sm` | `md` | `lg`), `isLoading` (boolean), `leftIcon` (ReactNode).
  * *Behavior:* Automatically suspends clicks during `isLoading` and substitutes text with a `<Spinner />` icon without shifting layout.
* **`Input.jsx`**
  * *Props:* `label` (string), `error` (string), `type` (text|password|email), `icon` (ReactNode).
  * *Behavior:* When an `error` prop is present, the bottom border turns red, and the label potentially utilizes Framer Motion to perform a gentle "shake" feedback.
* **`ProductCard.jsx`**
  * *Props:* `product` (Object), `onQuickBuy` (function).
  * *Structure:* Image wrapper occupying top 60%. Title, Category subtitle, and Price underneath. Hover triggers a quick CTA overlay (e.g. Add to Cart).
  * *Behavior:* Utilizes Framer Motion (`whileHover={{ y: -4, shadow: 'xl' }}`) for dynamic feeling.
* **`Modal.jsx`**
  * *Props:* `isOpen` (boolean), `onClose` (function), `title` (string), `footerActions` (ReactNode).
  * *Structure:* `<div />` overlay blanket across `100vw/100vh` locked via `z-50`. Captures `Esc` key down event to trigger `onClose`.
* **`DataTable.jsx`**
  * *Props:* `columns` (Array mapping keys to human labels), `data` (Array), `isLoading` (boolean).
  * *Behavior:* Standardized container eliminating boilerplate maps. Handles "No data available" gracefully with a nice icon.
* **`ToastAlert.jsx` (or implementation of `react-hot-toast`)**
  * *Props:* `type` (success|error), `message` (string).
  * *Behavior:* Called implicitly using the `useUIStore()`. Floats gracefully on the top right, disappearing after a preset 3500ms timeout unless hovered over.

### 🏠 Structural Elements (`/src/components/layout`)
* **`MainLayout.jsx`**
  * *Usage:* Wraps generic customer-facing surfaces (`/home`, `/products`).
  * *Structure:* Injects the `<Navbar />`, wraps content in a `<main className="container max-w-7xl">` utility, and stamps `<Footer />` down at the bottom.
* **`DashboardLayout.jsx`**
  * *Usage:* Wraps secured system portal surfaces.
  * *Structure:* A two-column grid. Left side holds the `Sidebar`, right side holds a scrollable main canvas. On mobile screens, the `Sidebar` acts like an off-canvas drawer activated via hamburger menu.
* **`Navbar.jsx`**
  * *State mapped:* Reads `cartCount` directly from `useCartStore` to display a live red notification badge over the Cart icon. Connects to `useAuthStore` to swap the generic "Login/Signup" buttons with an animated "User Hub Avatar Dropdown".
* **`Sidebar.jsx`**
  * *Logic:* Dynamically renders different navigation `<Link>` collections (e.g., "My Account/Orders" vs "Nursery Dashboard") entirely reliant on the `role` grabbed from `useAuthStore` runtime data.

---

## 🎨 8. UI/UX Strategy
* **Layout Design Approach:** Mobile-first, leveraging Tailwind CSS grid and flex utilities. The theme will feature eco-centric palettes (greens, earthy tones) paired with modern neutral backgrounds.
* **Responsiveness:** Ensure seamless scaling through Tailwind breakpoints (`md`, `lg`, `xl`). Dashboards will shift from grids to scrollable stacks on mobile devices.
* **Animation Plan (Framer Motion):**
  * Implementation of subtle page transition fades (`initial={{ opacity: 0 }} animate={{ opacity: 1 }}`).
  * Staggered list animations for product catalogs (`transition={{ staggerChildren: 0.1 }}`).
  * Micro-interactions on buttons and cards (`whileHover={{ scale: 1.02, y: -2 }}`).

---

## ⚠️ 9. Error Handling Strategy
* **Global Error Handling:** Implement an Axios interceptor response handler. If a `401 Unauthorized` is captured, force a silent logout and redirect back to login. For server errors (`500+`), display a generic safe message.
* **API Failure Handling:** Wrap API service calls in `try/catch`. Standardize the parsed error return object: `{ success: false, error: "Human readable message" }`.
* **User Feedback:** Utilize a toast notification library (like `react-hot-toast`) for non-intrusive feedback (e.g., "Item added to cart", "Network error"). Use inline form validation alerts for input errors.

---

## 🔄 10. Data Flow Explanation
**Backend → API → Service → State → UI**
1. **Backend:** Spring Boot APIs process databases and return JSON.
2. **API (Axios):** React initiates the request via the Axios client, injecting authentication headers automatically.
3. **Service:** Domain-specific files (e.g., `productService.js`) encapsulate the Axios functions to maintain clean components.
4. **State (Zustand):** Zustand stores invoke these service functions, wait for the response, and mutate their state tree (e.g., updating the cart array).
5. **UI (React):** The React components, subscribed to the Zustand store, automatically re-render the view with the fresh data.

---

## 🌍 11. Environment Configuration
* **.env Variables:**
  * `VITE_API_BASE_URL` (e.g., `http://localhost:8080` for backend connection)
  * `VITE_RAZORPAY_KEY_ID` (Public key for loading the Razorpay checkout script)
* **API Base URL Setup:**
  ```javascript
  import axios from 'axios';
  const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL + '/api',
    timeout: 10000,
    headers: { 'Content-Type': 'application/json' }
  });
  ```

---

## 🧪 12. Future Enhancements
* **Suggested Improvements:**
  * Implement infinite scrolling or sophisticated pagination for product logs using React Query mapping.
  * Integrate WebSockets to provide real-time updates for order status and payment confirmations.
* **Missing APIs (Potential Additions):**
  * Product reviews and star ratings endpoint.
  * A user wishlist endpoint (`/api/user/wishlist`).
  * File upload endpoint specifically for user profiles or nursery banners (often handled differently from product bulk uploads).
