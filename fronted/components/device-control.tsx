"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Slider } from "@/components/ui/slider"
import { Badge } from "@/components/ui/badge"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog"
import { Lightbulb, Fan, Minus, Plus } from "lucide-react"

interface DeviceControlProps {
  type: "light" | "fan"
  isOn: boolean
  onToggle: (value: boolean) => void
}

export function DeviceControl({ type, isOn, onToggle }: DeviceControlProps) {
  const [showDialog, setShowDialog] = useState(false)
  const [intensity, setIntensity] = useState(type === "light" ? 75 : 60)

  const config = type === "light" 
    ? {
        icon: Lightbulb,
        name: "补光灯",
        unit: "亮度",
        color: "amber",
        bgActive: "bg-amber-100",
        bgInactive: "bg-gray-100",
        iconActive: "text-amber-600",
        iconInactive: "text-gray-400",
        badgeActive: "bg-amber-100 text-amber-700",
        badgeInactive: "bg-gray-100 text-gray-600",
      }
    : {
        icon: Fan,
        name: "风扇",
        unit: "风速",
        color: "blue",
        bgActive: "bg-blue-100",
        bgInactive: "bg-gray-100",
        iconActive: "text-blue-600",
        iconInactive: "text-gray-400",
        badgeActive: "bg-blue-100 text-blue-700",
        badgeInactive: "bg-gray-100 text-gray-600",
      }

  const Icon = config.icon

  const getSpeedLabel = () => {
    if (!isOn) return "已关闭"
    if (intensity < 30) return "低速"
    if (intensity < 70) return "中速"
    return "高速"
  }

  const getBrightnessLabel = () => {
    if (!isOn) return "已关闭"
    if (intensity < 30) return "微光"
    if (intensity < 70) return "适中"
    return "明亮"
  }

  const handleQuickAdjust = (delta: number) => {
    setIntensity((prev) => Math.max(0, Math.min(100, prev + delta)))
  }

  return (
    <>
      <div 
        className="flex items-center gap-3 flex-1 cursor-pointer hover:opacity-80 transition-opacity"
        onClick={() => setShowDialog(true)}
      >
        <div className={`p-2 rounded-xl ${isOn ? config.bgActive : config.bgInactive}`}>
          <Icon 
            className={`h-5 w-5 ${isOn ? config.iconActive : config.iconInactive} ${
              isOn && type === "fan" ? "animate-spin" : ""
            }`} 
            style={isOn && type === "fan" ? { animationDuration: `${Math.max(0.3, 2 - intensity / 50)}s` } : undefined}
          />
        </div>
        <div className="flex-1 min-w-[80px]">
          <p className="font-medium text-sm">{config.name}</p>
          <p className="text-xs text-muted-foreground">
            {type === "light" ? getBrightnessLabel() : getSpeedLabel()}
          </p>
        </div>
        <Badge variant="secondary" className={isOn ? config.badgeActive : config.badgeInactive}>
          {isOn ? `${intensity}%` : "关闭"}
        </Badge>
      </div>

      <Dialog open={showDialog} onOpenChange={setShowDialog}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <Icon 
                className={`h-5 w-5 ${config.iconActive} ${
                  isOn && type === "fan" ? "animate-spin" : ""
                }`}
                style={isOn && type === "fan" ? { animationDuration: `${Math.max(0.3, 2 - intensity / 50)}s` } : undefined}
              />
              {config.name}控制
            </DialogTitle>
            <DialogDescription>
              调节{config.name}的{config.unit}和开关状态
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-6 py-4">
            {/* 开关控制 */}
            <div className="flex items-center justify-between p-4 rounded-xl bg-muted/50">
              <div className="flex items-center gap-3">
                <div className={`p-3 rounded-xl ${isOn ? config.bgActive : config.bgInactive}`}>
                  <Icon 
                    className={`h-6 w-6 ${isOn ? config.iconActive : config.iconInactive} ${
                      isOn && type === "fan" ? "animate-spin" : ""
                    }`}
                    style={isOn && type === "fan" ? { animationDuration: `${Math.max(0.3, 2 - intensity / 50)}s` } : undefined}
                  />
                </div>
                <div>
                  <p className="font-medium">电源开关</p>
                  <p className="text-sm text-muted-foreground">
                    {isOn ? "设备运行中" : "设备已关闭"}
                  </p>
                </div>
              </div>
              <Button
                variant={isOn ? "default" : "outline"}
                size="lg"
                onClick={() => onToggle(!isOn)}
              >
                {isOn ? "关闭" : "开启"}
              </Button>
            </div>

            {/* 强度调节 - 始终显示，但关闭时禁用 */}
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <label className="text-sm font-medium">{config.unit}调节</label>
                <div className="flex items-center gap-2">
                  <Button
                    variant="outline"
                    size="icon"
                    className="h-8 w-8"
                    onClick={() => handleQuickAdjust(-10)}
                    disabled={!isOn || intensity <= 0}
                  >
                    <Minus className="h-4 w-4" />
                  </Button>
                  <span className="text-2xl font-bold min-w-[60px] text-center">
                    {intensity}%
                  </span>
                  <Button
                    variant="outline"
                    size="icon"
                    className="h-8 w-8"
                    onClick={() => handleQuickAdjust(10)}
                    disabled={!isOn || intensity >= 100}
                  >
                    <Plus className="h-4 w-4" />
                  </Button>
                </div>
              </div>

              <Slider
                value={[intensity]}
                onValueChange={(value) => setIntensity(value[0])}
                min={0}
                max={100}
                step={1}
                className="w-full"
                disabled={!isOn}
              />

              <div className="flex justify-between text-xs text-muted-foreground">
                <span>0%</span>
                <span>50%</span>
                <span>100%</span>
              </div>

              {/* 预设快捷按钮 */}
              <div className="grid grid-cols-3 gap-2 pt-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setIntensity(25)}
                  className={intensity === 25 ? "border-primary" : ""}
                  disabled={!isOn}
                >
                  低 (25%)
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setIntensity(50)}
                  className={intensity === 50 ? "border-primary" : ""}
                  disabled={!isOn}
                >
                  中 (50%)
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setIntensity(100)}
                  className={intensity === 100 ? "border-primary" : ""}
                  disabled={!isOn}
                >
                  高 (100%)
                </Button>
              </div>
              
              {!isOn && (
                <p className="text-xs text-muted-foreground text-center pt-2">
                  请先开启设备以调节{config.unit}
                </p>
              )}
            </div>

            {/* 状态提示 */}
            <div className="p-3 rounded-xl bg-muted/50 text-sm">
              <p className="text-muted-foreground">
                {type === "light" ? (
                  <>
                    当前{config.unit}：<span className="font-medium text-foreground">{getBrightnessLabel()}</span>
                    {isOn && ` · 功率约 ${Math.round(intensity * 0.15)}W`}
                  </>
                ) : (
                  <>
                    当前{config.unit}：<span className="font-medium text-foreground">{getSpeedLabel()}</span>
                    {isOn && ` · 转速约 ${Math.round(intensity * 20)} RPM`}
                  </>
                )}
              </p>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </>
  )
}
