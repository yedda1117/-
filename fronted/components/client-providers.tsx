"use client"

/**
 * ClientProviders
 * 将所有需要 "use client" 的 Provider 集中在此，
 * 供 Server Component（layout.tsx）包裹 children 使用。
 */

import type { ReactNode } from "react"
import { PlantSelectionProvider } from "@/context/plant-selection"
import { GlobalNavbar } from "@/components/global-navbar"
import { Toaster } from "@/components/ui/toaster"

export function ClientProviders({ children }: { children: ReactNode }) {
  return (
    <PlantSelectionProvider>
      <GlobalNavbar />
      {children}
      <Toaster />
    </PlantSelectionProvider>
  )
}
