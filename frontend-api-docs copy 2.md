# GreenSelf Frontend API Requirements

This document outlines the REST API endpoints the frontend application will integrate with to provide the GreenSelf marketplace experience.

## Base URL
Ensure your API client (like Axios or Fetch) is configured with a base URL of `http://localhost:8080/api` (or environment-equivalent).

## 1. User Authentication
| Method | Endpoint | Description | Request Body Example | Response / Behavior | Require Auth Token |
|---|---|---|---|---|---|
| POST | `/auth/login` | Authenticate user | `{ "username": "...", "password": "..." }` | Returns `{ "token": "...", "username": "..." }`. Save `token` in LocalStorage or cookies. | No |
| POST | `/user/register` | Create account | `{ "username": "...", "email": "...", "password": "...", "role": "USER" }` | Returns User ID and confirmation. | No |
| GET | `/user/me` | Fetch active user | - | Validates the token and returns user info. | Yes |

## 2. Browsing Products & Plants 
This is the core logic for rendering catalogs and the "all plants" requirements.

| Method | Endpoint | Description | Query Parameters | Response / Behavior | Require Auth Token |
|---|---|---|---|---|---|
| GET | `/products/public` | Get all products | `page=0`, `size=10`, `sort=name,asc` | Returns a paginated list of summary `ProductResponseDto`. | No |
| GET | `/products/search` | Search product | `keyword="..."`, `page=0`, `size=10` | Returns paginated matched products. | No |
| GET | `/products/category/{category}` | Filter by category| `page=0` | Returns filtered products. | No |
| GET | `/products/{productId}` | Single Product Info| - | Returns `ProductResponseDto`. | No |
| GET | `/products/details/{productId}` | **Deep Product & Plant Details** | - | Returns `ProductDetailedResponseDto` (Combines product pricing with botanical plant info). **(Crucial for "all plant" detailed view)** | No |
| GET | `/plants/search` | Search plant db | `name="..."`, `page=0` | Returns raw plant definitions. (Used if you have a purely botanical search page). | No |

## 3. Shopping Cart Management
_Ensure `Authorization: Bearer <token>` is sent for all headers._

| Method | Endpoint | Description | Path Variable / Body | Response / Behavior | Require Auth Token |
|---|---|---|---|---|---|
| GET | `/user/cart` | View Cart | - | Returns user's cart and total values. | Yes |
| POST | `/user/cart` | Add to Cart | `{ "productId": 123, "quantity": 1 }` | Confirmation message. | Yes |
| PATCH | `/user/cart/{productId}` | Update quantity | Query: `?count=3` | Updates quantity in the cart (Max limit enforced). | Yes |
| DELETE| `/user/cart/{itemId}` | Remove Item | Path: `{itemId}` | Removes from cart. | Yes |

## 4. Checkout and Orders
| Method | Endpoint | Description | Request Body / Path | Response / Behavior | Require Auth Token |
|---|---|---|---|---|---|
| POST | `/order/create` | Checkout Cart | `{ "street": "...", "city": "...", "zipCode": "..." }` (Address object) | Completes transaction, returns `OrderResponse`. | Yes |
| GET | `/user/orders` | List Orders | - | Returns a list of past orders. | Yes |
| GET | `/user/orders/{orderId}` | Order Details | Path: `{orderId}` | Deep dive into a specific order's status and tracking. | Yes |
