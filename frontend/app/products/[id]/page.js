"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
import { apiFetch, authHeaders } from "@/lib/api";
import { getStoredAuth } from "@/lib/auth";
import { formatPrice } from "@/lib/currency";

export default function ProductDetailsPage() {
  const params = useParams();
  const [product, setProduct] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    loadProduct();
  }, [params.id]);

  async function loadProduct() {
    setLoading(true);
    setError("");
    try {
      const data = await apiFetch(`/api/products/${params.id}`);
      setProduct(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleAddToCart() {
    const auth = getStoredAuth();
    if (!auth?.token) {
      window.location.href = "/login";
      return;
    }

    try {
      await apiFetch("/api/cart/items", {
        method: "POST",
        headers: authHeaders(auth.token),
        body: JSON.stringify({
          productId: product.id,
          quantity: Number(quantity)
        })
      });
      setMessage("Product added to cart");
    } catch (err) {
      setError(err.message);
    }
  }

  if (loading) {
    return <p>Loading product...</p>;
  }

  if (error) {
    return <p className="error-text">{error}</p>;
  }

  return (
    <section className="panel">
      <p className="eyebrow">{product.categoryName}</p>
      <h1 className="section-title">{product.name}</h1>
      <p className="muted">{product.description}</p>

      <div className="summary-row">
        <span>Price</span>
        <strong>{formatPrice(product.currentPrice)}</strong>
      </div>
      <div className="summary-row">
        <span>Stock</span>
        <strong>{product.stock}</strong>
      </div>

      <label className="field">
        Quantity
        <input
          className="quantity-input"
          type="number"
          min="1"
          max={product.stock}
          value={quantity}
          onChange={(event) => setQuantity(event.target.value)}
        />
      </label>

      <div className="inline-actions">
        <button type="button" className="button" onClick={handleAddToCart}>
          Add To Cart
        </button>
        <Link href="/" className="button ghost">
          Back
        </Link>
      </div>

      {message && <p className="helper-text">{message}</p>}
    </section>
  );
}
