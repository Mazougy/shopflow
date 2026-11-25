"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { clearAuth, getStoredAuth } from "@/lib/auth";

export default function Navbar() {
  const [auth, setAuth] = useState(null);

  useEffect(() => {
    setAuth(getStoredAuth());
    const syncAuth = () => setAuth(getStoredAuth());
    window.addEventListener("storage", syncAuth);
    window.addEventListener("focus", syncAuth);
    return () => {
      window.removeEventListener("storage", syncAuth);
      window.removeEventListener("focus", syncAuth);
    };
  }, []);

  function handleLogout() {
    clearAuth();
    setAuth(null);
    window.location.href = "/";
  }

  return (
    <header className="navbar">
      <Link href="/" className="brand">
        ShopFlow
      </Link>
      <nav className="nav-links">
        <Link href="/">Products</Link>
        {auth && <Link href="/cart">Cart</Link>}
        {auth && <Link href="/orders">My Orders</Link>}
        {!auth ? (
          <>
            <Link href="/login">Login</Link>
            <Link href="/register" className="button secondary small">
              Register
            </Link>
          </>
        ) : (
          <>
            <button type="button" className="button ghost small" onClick={handleLogout}>
              Logout
            </button>
          </>
        )}
      </nav>
    </header>
  );
}
