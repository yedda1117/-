"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { MapPin, Satellite, Navigation, Signal } from "lucide-react"

interface GPSData {
  latitude: number
  longitude: number
  altitude: number
  accuracy: number
  satellites: number
  status: "connected" | "searching" | "disconnected"
  lastUpdate: string
}

export function GPSStatus() {
  const [gpsData, setGpsData] = useState<GPSData>({
    latitude: 39.9042,
    longitude: 116.4074,
    altitude: 43.5,
    accuracy: 5.2,
    satellites: 8,
    status: "connected",
    lastUpdate: new Date().toLocaleTimeString("zh-CN", { hour: "2-digit", minute: "2-digit" }),
  })

  // 模拟 GPS 数据更新
  useEffect(() => {
    const interval = setInterval(() => {
      setGpsData((prev) => ({
        ...prev,
        latitude: prev.latitude + (Math.random() - 0.5) * 0.0001,
        longitude: prev.longitude + (Math.random() - 0.5) * 0.0001,
        accuracy: Math.max(3, Math.min(10, prev.accuracy + (Math.random() - 0.5) * 0.5)),
        lastUpdate: new Date().toLocaleTimeString("zh-CN", { hour: "2-digit", minute: "2-digit" }),
      }))
    }, 5000)

    return () => clearInterval(interval)
  }, [])

  const getStatusConfig = () => {
    switch (gpsData.status) {
      case "connected":
        return { label: "已连接", color: "bg-green-100 text-green-700", icon: "text-green-600" }
      case "searching":
        return { label: "搜索中", color: "bg-yellow-100 text-yellow-700", icon: "text-yellow-600" }
      case "disconnected":
        return { label: "未连接", color: "bg-gray-100 text-gray-700", icon: "text-gray-600" }
    }
  }

  const statusConfig = getStatusConfig()

  return (
    <Card>
      <CardHeader className="pb-3">
        <CardTitle className="flex items-center gap-2 text-base">
          <MapPin className="h-4 w-4 text-primary" />
          GPS 定位状态
          <Badge variant="secondary" className={`ml-auto text-xs font-normal ${statusConfig.color}`}>
            {statusConfig.label}
          </Badge>
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-3">
        {/* 坐标信息 */}
        <div className="flex items-start gap-3 p-3 rounded-xl bg-muted/50">
          <Navigation className={`h-5 w-5 mt-0.5 ${statusConfig.icon}`} />
          <div className="flex-1 space-y-1">
            <p className="text-xs text-muted-foreground">坐标位置</p>
            <p className="text-sm font-mono">
              {gpsData.latitude.toFixed(6)}°N
            </p>
            <p className="text-sm font-mono">
              {gpsData.longitude.toFixed(6)}°E
            </p>
          </div>
        </div>

        {/* 海拔高度 */}
        <div className="flex items-center justify-between p-3 rounded-xl bg-muted/50">
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-lg bg-blue-100">
              <MapPin className="h-4 w-4 text-blue-600" />
            </div>
            <div>
              <p className="text-xs text-muted-foreground">海拔高度</p>
              <p className="text-sm font-semibold">{gpsData.altitude.toFixed(1)} m</p>
            </div>
          </div>
          <Badge variant="outline" className="text-xs">
            精度 ±{gpsData.accuracy.toFixed(1)}m
          </Badge>
        </div>

        {/* 卫星信号 */}
        <div className="flex items-center justify-between p-3 rounded-xl bg-muted/50">
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-lg bg-purple-100">
              <Satellite className="h-4 w-4 text-purple-600" />
            </div>
            <div>
              <p className="text-xs text-muted-foreground">卫星数量</p>
              <p className="text-sm font-semibold">{gpsData.satellites} 颗</p>
            </div>
          </div>
          <div className="flex items-center gap-1">
            {[...Array(4)].map((_, i) => (
              <div
                key={i}
                className={`w-1 rounded-full ${
                  i < Math.floor(gpsData.satellites / 3)
                    ? "bg-green-500 h-3"
                    : "bg-gray-300 h-2"
                }`}
              />
            ))}
          </div>
        </div>

        {/* 更新时间 */}
        <div className="flex items-center justify-between pt-2 border-t">
          <div className="flex items-center gap-2">
            <Signal className="h-3 w-3 text-muted-foreground" />
            <span className="text-xs text-muted-foreground">最后更新</span>
          </div>
          <span className="text-xs font-mono text-muted-foreground">{gpsData.lastUpdate}</span>
        </div>
      </CardContent>
    </Card>
  )
}
