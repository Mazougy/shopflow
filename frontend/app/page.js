"use client";

import { useEffect, useState } from "react";
import ProductCard from "@/components/ProductCard";
import { apiFetch } from "@/lib/api";

export default function HomePage() {
  const heroTitle = "Simple shopping flow, clean enough to demo tomorrow.";
  const [products, setProducts] = useState([]);
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);
  const [meta, setMeta] = useState({ totalPages: 0, first: true, last: true });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [displayTitle, setDisplayTitle] = useState("");

  useEffect(() => {
    loadProducts();
  }, [page]);

  useEffect(() => {
    if (typeof document !== "undefined" && document.activeElement instanceof HTMLElement) {
      document.activeElement.blur();
    }
  }, []);

  useEffect(() => {
    let cancelled = false;

    async function runTyping() {
      for (let index = 1; index <= heroTitle.length; index += 1) {
        if (cancelled) {
          return;
        }

        setDisplayTitle(heroTitle.slice(0, index));

        await new Promise((resolve) => {
          window.setTimeout(resolve, 42);
        });
      }
    }

    runTyping();

    return () => {
      cancelled = true;
    };
  }, []);

  async function loadProducts(currentSearch = search, currentPage = page) {
    setLoading(true);
    setError("");
    try {
      const query = new URLSearchParams({
        page: String(currentPage),
        size: "8",
        ...(currentSearch ? { search: currentSearch } : {})
      });
      const data = await apiFetch(`/api/products?${query.toString()}`);
      setProducts(data.content);
      setMeta(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  function handleSearch(event) {
    event.preventDefault();
    setPage(0);
    loadProducts(search, 0);
  }

  return (
    <section className="home-page">
      <div className="hero hero-home">
        <h1 className="hero-title">
          <span>{displayTitle}</span>
          {displayTitle.length < heroTitle.length && <span className="typing-caret" aria-hidden="true" />}
        </h1>
        <p>
          Browse products, sign in, add items to cart, and place an order. Admin product and category management
          is available through Swagger or Postman for a fast classroom demo.
        </p>
      </div>

      <form className="search-row" onSubmit={handleSearch}>
        <input
          className="search-input"
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          placeholder="Search products by name"
        />
        <button type="submit" className="button">
          Search
        </button>
      </form>

      {loading && <p>Loading products...</p>}
      {error && <p className="error-text">{error}</p>}

      {!loading && !error && (
        <>
          <div className="grid">
            {products.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>

          {products.length === 0 && <div className="card empty-state">No products found.</div>}

          <div className="top-row pagination-row">
            <span className="muted">
              Page {meta.page + 1} / {Math.max(meta.totalPages, 1)}
            </span>
            <div className="inline-actions">
              <button type="button" className="button ghost small" disabled={meta.first} onClick={() => setPage(page - 1)}>
                Previous
              </button>
              <button type="button" className="button ghost small" disabled={meta.last} onClick={() => setPage(page + 1)}>
                Next
              </button>
            </div>
          </div>
        </>
      )}
    </section>
  );
}
