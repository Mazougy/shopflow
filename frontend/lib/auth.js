"use client";

export function getStoredAuth() {
  if (typeof window === "undefined") {
    return null;
  }

  const raw = localStorage.getItem("shopflow_auth");
  return raw ? JSON.parse(raw) : null;
}

export function saveAuth(data) {
  localStorage.setItem("shopflow_auth", JSON.stringify(data));
}

export function clearAuth() {
  localStorage.removeItem("shopflow_auth");
}
