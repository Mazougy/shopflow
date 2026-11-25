"use client";

import { useEffect, useState } from "react";
import { apiFetch, authHeaders } from "@/lib/api";
import { getStoredAuth } from "@/lib/auth";
import { formatPrice } from "@/lib/currency";

export default function CartPage() {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const auth = getStoredAuth();
    if (!auth?.token) {
      window.location.href = "/login";
      return;
    }
    loadCart(auth.token);
  }, []);

  async function loadCart(token) {
    setLoading(true);
    setError("");
    try {
      const data = await apiFetch("/api/cart", {
        headers: authHeaders(token)
      });
      setCart(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function updateQuantity(itemId, quantity) {
    const auth = getStoredAuth();
    try {
      const data = await apiFetch(`/api/cart/items/${itemId}`, {
        method: "PUT",
        headers: authHeaders(auth.token),
        body: JSON.stringify({ quantity: Number(quantity) })
      });
      setCart(data);
    } catch (err) {
      setError(err.message);
    }
  }

  async function removeItem(itemId) {
    const auth = getStoredAuth();
    try {
      const data = await apiFetch(`/api/cart/items/${itemId}`, {
        method: "DELETE",
        headers: authHeaders(auth.token)
      });
      setCart(data);
    } catch (err) {
      setError(err.message);
    }
  }

  async function checkout() {
    const auth = getStoredAuth();
    try {
      const order = await apiFetch("/api/orders", {
        method: "POST",
        headers: authHeaders(auth.token)
      });
      window.location.href = `/orders?created=${order.orderNumber}`;
    } catch (err) {
      setError(err.message);
    }
  }

  if (loading) {
    return <p>Loading cart...</p>;
  }

  return (
    <section>
      <div className="hero">
        <p className="eyebrow">Customer Space</p>
        <h1 className="section-title">My Cart</h1>
        <p>Adjust quantities, review totals, and checkout into an order.</p>
      </div>

      {error && <p className="error-text">{error}</p>}

      {!cart?.items?.length ? (
        <div className="card">Your cart is empty.</div>
      ) : (
        <div className="layout-split">
          <div className="card cart-items">
            {cart.items.map((item) => (
              <div className="cart-row" key={item.id}>
                <div className="cart-item-info">
                  <strong>{item.productName}</strong>
                  <p className="muted">{formatPrice(item.unitPrice)} each</p>
                </div>
                <input
                  className="search-input quantity-input"
                  type="number"
                  min="1"
                  value={item.quantity}
                  onChange={(event) => updateQuantity(item.id, event.target.value)}
                />
                <strong className="price-value">{formatPrice(item.lineTotal)}</strong>
                <button type="button" className="button ghost small" onClick={() => removeItem(item.id)}>
                  Remove
                </button>
              </div>
            ))}
          </div>

          <aside className="card">
            <div className="summary-row">
              <span>Subtotal</span>
              <strong>{formatPrice(cart.subTotal)}</strong>
            </div>
            <div className="summary-row">
              <span>Shipping</span>
              <strong>{formatPrice(cart.shippingFee)}</strong>
            </div>
            <div className="summary-row">
              <span>Total</span>
              <strong>{formatPrice(cart.total)}</strong>
            </div>
            <button type="button" className="button" style={{ width: "100%", marginTop: "16px" }} onClick={checkout}>
              Checkout
            </button>
          </aside>
        </div>
      )}
    </section>
  );
}
