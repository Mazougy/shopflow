"use client";

import { useEffect, useState } from "react";
import { apiFetch, authHeaders } from "@/lib/api";
import { getStoredAuth } from "@/lib/auth";
import { formatPrice } from "@/lib/currency";

export default function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [createdOrderNumber, setCreatedOrderNumber] = useState("");

  useEffect(() => {
    if (typeof window !== "undefined") {
      const params = new URLSearchParams(window.location.search);
      setCreatedOrderNumber(params.get("created") || "");
    }

    const auth = getStoredAuth();
    if (!auth?.token) {
      window.location.href = "/login";
      return;
    }
    loadOrders(auth.token);
  }, []);

  async function loadOrders(token) {
    setLoading(true);
    setError("");
    try {
      const data = await apiFetch("/api/orders/my", {
        headers: authHeaders(token)
      });
      setOrders(data);
      if (data.length > 0) {
        setSelectedOrder(data[0]);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function showDetails(orderId) {
    const auth = getStoredAuth();
    try {
      const data = await apiFetch(`/api/orders/${orderId}`, {
        headers: authHeaders(auth.token)
      });
      setSelectedOrder(data);
    } catch (err) {
      setError(err.message);
    }
  }

  async function cancelOrder(orderId) {
    const auth = getStoredAuth();
    try {
      const data = await apiFetch(`/api/orders/${orderId}/cancel`, {
        method: "PUT",
        headers: authHeaders(auth.token)
      });
      setSelectedOrder(data);
      loadOrders(auth.token);
    } catch (err) {
      setError(err.message);
    }
  }

  if (loading) {
    return <p>Loading orders...</p>;
  }

  return (
    <section>
      <div className="hero">
        <p className="eyebrow">Order Tracking</p>
        <h1 className="section-title">My Orders</h1>
        {createdOrderNumber && (
          <p className="helper-text">Order created successfully: {createdOrderNumber}</p>
        )}
      </div>

      {error && <p className="error-text">{error}</p>}

      <div className="layout-split">
        <div className="orders-list">
          {orders.length === 0 && <div className="card">No orders yet.</div>}
          {orders.map((order) => (
            <article key={order.id} className="card order-card">
              <div className="top-row">
                <strong>{order.orderNumber}</strong>
                <span className="tag">{order.status}</span>
              </div>
              <p className="muted">{new Date(order.createdAt).toLocaleString()}</p>
              <strong className="price-value">{formatPrice(order.totalTTC)}</strong>
              <button type="button" className="button ghost small" onClick={() => showDetails(order.id)}>
                View Details
              </button>
            </article>
          ))}
        </div>

        <aside className="card">
          {!selectedOrder ? (
            <p>Select an order to see details.</p>
          ) : (
            <>
              <div className="top-row">
                <div>
                  <p className="eyebrow">Selected Order</p>
                  <h2>{selectedOrder.orderNumber}</h2>
                </div>
                <span className="tag">{selectedOrder.status}</span>
              </div>

              {selectedOrder.items.map((item) => (
                <div className="summary-row" key={item.id}>
                  <span>
                    {item.productName} x {item.quantity}
                  </span>
                  <strong className="price-value">{formatPrice(item.lineTotal)}</strong>
                </div>
              ))}

              <div className="summary-row">
                <span>Subtotal</span>
                <strong>{formatPrice(selectedOrder.subTotal)}</strong>
              </div>
              <div className="summary-row">
                <span>Shipping</span>
                <strong>{formatPrice(selectedOrder.shippingFee)}</strong>
              </div>
              <div className="summary-row">
                <span>Total</span>
                <strong>{formatPrice(selectedOrder.totalTTC)}</strong>
              </div>

              {selectedOrder.status === "PENDING" && (
                <button type="button" className="button" style={{ marginTop: "16px" }} onClick={() => cancelOrder(selectedOrder.id)}>
                  Cancel Order
                </button>
              )}
            </>
          )}
        </aside>
      </div>
    </section>
  );
}
