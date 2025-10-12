import type { ReactNode } from "react";

export default function DevOnly({ children }: { children: ReactNode }) {
  return import.meta.env.DEV ? children : null;
}