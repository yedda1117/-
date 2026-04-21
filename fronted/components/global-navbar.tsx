"use client"

/**
 * GlobalNavbar
 * 全局导航栏，包含植物选择下拉框。
 * 仅在需要显示的页面（/home、/calendar、/chat）渲染植物选择器，
 * 其他页面（/login、/register、/dashboard 等）不显示选择器。
 */

import { useEffect } from "react"
import { usePathname } from "next/navigation"
import { Leaf } from "lucide-react"
import { NavHeader } from "@/components/nav-header"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { usePlantSelection } from "@/context/plant-selection"

// 需要显示植物选择器的路由前缀
const PLANT_SELECTOR_PATHS = ["/home", "/calendar", "/chat","/settings"]

// 不显示导航栏的路由（登录/注册页）
const HIDDEN_NAV_PATHS = ["/login", "/register", "/"]

export function GlobalNavbar() {
  const pathname = usePathname()
  // 假设你的 Context 中已经包含了 setPlants 方法用于更新全局列表
  const { selectedPlantId, setSelectedPlantId, plants, setPlants } = usePlantSelection()

  // 动态获取植物列表逻辑
  useEffect(() => {
    const fetchPlants = async () => {
      try {
        // 从本地存储获取 Token，保持与 page.tsx 逻辑一致
        const token = window.localStorage.getItem("plantcloud_token")
        const res = await fetch("/api/plants", {
          method: "GET",
          headers: {
            "Authorization": token ? `Bearer ${token}` : ""
          }
        })
        
        const result = await res.json()
        
        // 如果后端返回 code 200 且含有数据，则更新 Context 中的植物列表
        if (result && Array.isArray(result)) {
        setPlants(result) 
      } else if (result && Array.isArray(result.data)) {
        setPlants(result.data)
      }
    } catch (err) {
      console.error("Navbar 抓取失败:", err)
    }
    }

    // 仅在非登录/注册页面时尝试调取数据
    if (!HIDDEN_NAV_PATHS.includes(pathname)) {
      fetchPlants()
    }
  }, [pathname, setPlants])

  // 登录/注册页不渲染导航栏
  if (HIDDEN_NAV_PATHS.includes(pathname)) {
    return null
  }

  const showPlantSelector = PLANT_SELECTOR_PATHS.some((p) => pathname.startsWith(p))

  return (
    <NavHeader
      rightSlot={
        showPlantSelector ? (
          <div className="flex items-center gap-2">
            <Leaf className="h-4 w-4 text-primary" />
            <span className="text-sm text-muted-foreground">当前植物：</span>
            <Select value={selectedPlantId} onValueChange={setSelectedPlantId}>
              <SelectTrigger className="w-40 h-8 text-sm">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {plants && plants.length > 0 ? (
                  plants.map((p) => (
                    <SelectItem key={p.id} value={p.id}>
                      {p.emoji} {p.name}
                    </SelectItem>
                  ))
                ) : (
                  <SelectItem value="none" disabled>暂无植物</SelectItem>
                )}
              </SelectContent>
            </Select>
          </div>
        ) : undefined
      }
    />
  )
}