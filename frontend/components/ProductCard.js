"use client";

import Link from "next/link";
import { useState } from "react";
import { apiFetch, authHeaders } from "@/lib/api";
import { getStoredAuth } from "@/lib/auth";
import { formatPrice } from "@/lib/currency";

export default function ProductCard({ product, onAdded }) {
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  async function addToCart() {
    const auth = getStoredAuth();
    if (!auth?.token) {
      window.location.href = "/login";
      return;
    }

    setLoading(true);
    setMessage("");
    try {
      await apiFetch("/api/cart/items", {
        method: "POST",
        headers: authHeaders(auth.token),
        body: JSON.stringify({ productId: product.id, quantity: 1 })
      });
      setMessage("Added to cart");
      if (onAdded) {
        onAdded();
      }
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <article className="card product-card">
      <div>
        <p className="eyebrow">{product.categoryName}</p>
        <h3>{product.name}</h3>
        <p className="muted">{product.description || "Simple product for the ShopFlow demo."}</p>
      </div>
      <div className="product-footer">
        <div className="price-block">
          <strong className="price-value">{formatPrice(product.currentPrice)}</strong>
          {product.promoPrice && <span className="price-old">{formatPrice(product.price)}</span>}
        </div>
        <div className="inline-actions">
          <Link href={`/products/${product.id}`} className="button ghost small">
            Details
          </Link>
          <button type="button" className="button small" onClick={addToCart} disabled={loading}>
            {loading ? "..." : "Add"}
          </button>
        </div>
      </div>
      {message && <p className="helper-text">{message}</p>}
    </article>
  );
}
