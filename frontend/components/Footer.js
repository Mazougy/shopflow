export default function Footer() {
  return (
    <footer className="site-footer">
      <div className="site-footer-inner">
        <section>
          <h2>About Us</h2>
          <p>
            ShopFlow is a student e-commerce project designed to present a simple shopping experience with clean
            product browsing, cart flow, and order management.
          </p>
        </section>

        <section>
          <h2>Contact</h2>
          <p>
            Email:{" "}
            <a className="footer-link" href="mailto:marzouguiadam82@gmail.com">
              marzouguiadam82@gmail.com
            </a>
          </p>
          <p>
            Phone:{" "}
            <a className="footer-link" href="tel:+21621214420">
              +216 21 214 420
            </a>
          </p>
        </section>

        <section className="footer-socials" aria-label="Social links">
          <a href="https://www.instagram.com/" aria-label="Instagram" target="_blank" rel="noreferrer">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <rect x="3.5" y="3.5" width="17" height="17" rx="4.5" ry="4.5" />
              <circle cx="12" cy="12" r="4" />
              <circle cx="17.4" cy="6.6" r="1.2" className="icon-fill" />
            </svg>
          </a>
          <a href="https://wa.me/21621214420" aria-label="WhatsApp" target="_blank" rel="noreferrer">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M12 4.2a7.8 7.8 0 0 0-6.7 11.9L4 20l4-1.2A7.8 7.8 0 1 0 12 4.2Z" />
              <path
                className="icon-fill"
                d="M9.2 8.5c.2-.5.4-.5.7-.5h.6c.2 0 .5 0 .7.5l.6 1.4c.1.3.1.5 0 .7l-.4.6c-.1.2-.2.4 0 .6.4.7 1 1.4 1.8 1.9.2.1.4.1.6 0l.6-.4c.2-.1.4-.1.7 0l1.3.6c.5.2.5.5.5.7v.6c0 .3-.1.5-.5.7-.5.2-1 .4-1.7.2a8.3 8.3 0 0 1-5.6-5.6c-.2-.7 0-1.2.2-1.7Z"
              />
            </svg>
          </a>
          <a href="https://www.facebook.com/" aria-label="Facebook" target="_blank" rel="noreferrer">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M13.5 21v-7h2.4l.4-3h-2.8V9.1c0-.9.2-1.5 1.5-1.5H16V5.1c-.4-.1-1.2-.1-2.1-.1-2.1 0-3.4 1.3-3.4 3.6V11H8v3h2.5v7h3Z" />
            </svg>
          </a>
        </section>
      </div>

      <div className="site-footer-bottom">
        <p>Copyright 2026 ShopFlow. All rights reserved.</p>
      </div>
    </footer>
  );
}
