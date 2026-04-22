"use client"

import { Badge } from "@/components/ui/badge"
import { cn } from "@/lib/utils"
import { Fan, Lightbulb } from "lucide-react"

interface DeviceControlProps {
  type: "light" | "fan"
  isOn: boolean | null
  onToggle: (value: boolean) => void
  disabled?: boolean
}

const deviceConfig = {
  light: {
    icon: Lightbulb,
    name: "补光灯",
    activeCard: "border-amber-200 bg-amber-50/70 hover:bg-amber-50",
    inactiveCard: "border-border bg-card hover:bg-muted/30",
    activeIconBox: "bg-amber-100 text-amber-600 ring-1 ring-amber-200/70",
    inactiveIconBox: "bg-muted text-muted-foreground",
    activeBadge: "border-amber-200 bg-amber-100 text-amber-700 hover:bg-amber-100",
    inactiveBadge: "border-border bg-background text-muted-foreground hover:bg-background",
  },
  fan: {
    icon: Fan,
    name: "风扇",
    activeCard: "border-sky-200 bg-sky-50/70 hover:bg-sky-50",
    inactiveCard: "border-border bg-card hover:bg-muted/30",
    activeIconBox: "bg-sky-100 text-sky-600 ring-1 ring-sky-200/70",
    inactiveIconBox: "bg-muted text-muted-foreground",
    activeBadge: "border-sky-200 bg-sky-100 text-sky-700 hover:bg-sky-100",
    inactiveBadge: "border-border bg-background text-muted-foreground hover:bg-background",
  },
} as const

export function DeviceControl({ type, isOn, onToggle, disabled = false }: DeviceControlProps) {
  const config = deviceConfig[type]
  const Icon = config.icon
  const active = isOn === true
  const known = isOn !== null
  const nextValue = !active
  const statusLabel = known ? (active ? "已开启" : "已关闭") : "状态同步中"

  const handleToggleClick = () => {
    console.info("[CTRL][UI] device control clicked", {
      target: type,
      currentOn: isOn,
      nextOn: nextValue,
      disabled,
    })

    if (disabled) {
      console.warn("[CTRL][UI] device control click ignored because command is pending", {
        target: type,
      })
      return
    }

    onToggle(nextValue)
  }

  return (
    <button
      type="button"
      className={cn(
        "group flex h-[72px] min-w-0 items-center gap-2.5 rounded-xl border px-3 py-2.5 text-left shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2",
        active ? config.activeCard : config.inactiveCard,
        disabled && "cursor-not-allowed opacity-70",
      )}
      aria-label={`${nextValue ? "开启" : "关闭"}${config.name}`}
      aria-disabled={disabled}
      data-control-target={type}
      data-control-current={active ? "ON" : known ? "OFF" : "UNKNOWN"}
      onClick={handleToggleClick}
    >
      <span
        className={cn(
          "flex h-9 w-9 shrink-0 items-center justify-center rounded-full transition-colors",
          active ? config.activeIconBox : config.inactiveIconBox,
        )}
      >
        <Icon className={cn("h-[18px] w-[18px]", active && type === "fan" && "animate-spin")} />
      </span>

      <span className="min-w-0 flex-1 leading-tight">
        <span className="block whitespace-nowrap text-sm font-semibold text-foreground">{config.name}</span>
        <span className="mt-1 block whitespace-nowrap text-xs text-muted-foreground">{statusLabel}</span>
      </span>

      <Badge
        variant="outline"
        className={cn(
          "flex h-7 w-11 shrink-0 items-center justify-center rounded-full px-0 text-[11px] font-semibold tracking-normal",
          active ? config.activeBadge : config.inactiveBadge,
        )}
      >
        {known ? (active ? "ON" : "OFF") : "--"}
      </Badge>
    </button>
  )
}
