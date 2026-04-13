"use client"

import { useState, useEffect } from "react"
import { NavHeader } from "@/components/nav-header"
import { PixelPlant, PlantState } from "@/components/pixel-plant"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Switch } from "@/components/ui/switch"
import { Badge } from "@/components/ui/badge"
import { ScrollArea } from "@/components/ui/scroll-area"
import {
  Thermometer,
  Droplets,
  Sun,
  User,
  AlertTriangle,
  Fan,
  Lightbulb,
  Activity,
} from "lucide-react"

// 模拟传感器数据
const mockSensorData = {
  temperature: 24.5,
  humidity: 65,
  light: 320,
  hasHuman: true,
  isFallen: false,
}

// 模拟日志数据
const mockLogs = [
  { time: "10:30", event: "开启风扇（温度>26°C）", type: "info" },
  { time: "10:05", event: "光照已达到设定值（320 lux）", type: "success" },
  { time: "10:00", event: "补光灯开启（光照<300 lux）", type: "info" },
  { time: "09:30", event: "异常：花盆摔倒了", type: "error" },
  { time: "09:15", event: "主人来访（人体红外检测）", type: "info" },
  { time: "08:45", event: "自动浇水完成", type: "success" },
  { time: "08:00", event: "系统启动，开始监测", type: "info" },
]

export default function HomePage() {
  const [lightOn, setLightOn] = useState(true)
  const [fanOn, setFanOn] = useState(false)
  const [plantState, setPlantState] = useState<PlantState>("healthy")
  const [sensorData, setSensorData] = useState(mockSensorData)

  // 根据环境数据计算植物状态
  useEffect(() => {
    if (sensorData.isFallen) {
      setPlantState("fallen")
    } else if (sensorData.temperature > 30) {
      setPlantState("hot")
    } else if (sensorData.temperature < 15) {
      setPlantState("cold")
    } else if (sensorData.humidity < 40) {
      setPlantState("thirsty")
    } else if (sensorData.light < 200) {
      setPlantState("dark")
    } else if (sensorData.hasHuman) {
      setPlantState("happy")
    } else {
      setPlantState("healthy")
    }
  }, [sensorData])

  // 模拟状态切换演示
  const cycleStates = () => {
    const states: PlantState[] = ["healthy", "happy", "thirsty", "hot", "cold", "dark", "fallen"]
    const currentIndex = states.indexOf(plantState)
    const nextIndex = (currentIndex + 1) % states.length
    setPlantState(states[nextIndex])
  }

  return (
    <div className="min-h-screen bg-background">
      <NavHeader />
      
      <main className="container mx-auto px-6 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
          
          {/* 左侧栏：植物动态日志 */}
          <div className="lg:col-span-3">
            <Card className="h-full">
              <CardHeader className="pb-3">
                <CardTitle className="flex items-center gap-2 text-base">
                  <Activity className="h-4 w-4 text-primary" />
                  植物动态日志
                </CardTitle>
              </CardHeader>
              <CardContent>
                <ScrollArea className="h-[400px] pr-4">
                  <div className="space-y-3">
                    {mockLogs.map((log, index) => (
                      <div
                        key={index}
                        className="flex items-start gap-3 p-3 rounded-xl bg-muted/50 hover:bg-muted transition-colors"
                      >
                        <span className="text-xs font-mono text-muted-foreground whitespace-nowrap">
                          [{log.time}]
                        </span>
                        <span className={`text-sm flex-1 ${
                          log.type === "error" ? "text-destructive" :
                          log.type === "success" ? "text-primary" :
                          "text-foreground"
                        }`}>
                          {log.event}
                        </span>
                      </div>
                    ))}
                  </div>
                </ScrollArea>
              </CardContent>
            </Card>
          </div>

          {/* 中间区域：植物主体 */}
          <div className="lg:col-span-6">
            <Card className="h-full">
              <CardContent className="flex flex-col items-center justify-center py-8">
                {/* 植物像素主体 - 添加外框并放大 */}
                <div className="relative mb-8 w-full flex justify-center">
                  {/* 外框容器 - 固定高度，确保植物和边框在可视区域内 */}
                  <div className="border-2 border-primary/20 rounded-3xl p-6 bg-gradient-to-br from-primary/5 to-transparent w-full max-w-md aspect-square flex items-center justify-center">
                    {/* 发光效果 */}
                    <div className="absolute inset-0 bg-primary/5 rounded-full blur-3xl scale-150" />
                    {/* 植物本身 - 适当放大，确保在框内 */}
                    <div className="scale-125">
                      <PixelPlant state={plantState} size="xl" />
                    </div>
                  </div>
                </div>
                
                {/* 状态演示按钮 */}
                <Button
                  variant="outline"
                  onClick={cycleStates}
                  className="mb-6"
                >
                  切换状态演示
                </Button>

                {/* 控制面板 - 排成一排 */}
                <div className="flex items-center justify-between w-full max-w-xl bg-muted/30 rounded-2xl p-4 border">
                  {/* 补光灯控制 */}
                  <div className="flex items-center gap-3 flex-1">
                    <div className={`p-2 rounded-xl ${lightOn ? "bg-amber-100" : "bg-gray-100"}`}>
                      <Lightbulb className={`h-5 w-5 ${lightOn ? "text-amber-600" : "text-gray-400"}`} />
                    </div>
                    <div className="flex-1 min-w-[80px]">
                      <p className="font-medium text-sm">补光灯</p>
                      <p className="text-xs text-muted-foreground">{lightOn ? "开启中" : "已关闭"}</p>
                    </div>
                    <Switch
                      checked={lightOn}
                      onCheckedChange={setLightOn}
                    />
                  </div>

                  <div className="w-px h-8 bg-border mx-2" />

                  {/* 风扇控制 */}
                  <div className="flex items-center gap-3 flex-1">
                    <div className={`p-2 rounded-xl ${fanOn ? "bg-blue-100" : "bg-gray-100"}`}>
                      <Fan className={`h-5 w-5 ${fanOn ? "text-blue-600 animate-spin" : "text-gray-400"}`} style={{ animationDuration: "1s" }} />
                    </div>
                    <div className="flex-1 min-w-[80px]">
                      <p className="font-medium text-sm">风扇</p>
                      <p className="text-xs text-muted-foreground">{fanOn ? "运转中" : "已关闭"}</p>
                    </div>
                    <Switch
                      checked={fanOn}
                      onCheckedChange={setFanOn}
                    />
                  </div>

                  <div className="w-px h-8 bg-border mx-2" />

                  {/* 设备状态 */}
                  <div className="flex items-center gap-3 flex-1">
                    <div className="p-2 rounded-xl bg-primary/10">
                      <div className="h-5 w-5 flex items-center justify-center">
                        <span className="h-2 w-2 rounded-full bg-green-500 animate-pulse" />
                      </div>
                    </div>
                    <div className="flex-1 min-w-[100px]">
                      <p className="font-medium text-sm">小熊派已连接</p>
                      <p className="text-xs text-muted-foreground">延时: 23ms</p>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* 右侧区域：监测与控制 */}
          <div className="lg:col-span-3 space-y-4">
            {/* 温度监测 */}
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="p-2 rounded-xl bg-orange-100">
                      <Thermometer className="h-5 w-5 text-orange-600" />
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">温度监测</p>
                      <p className="text-xl font-bold">{sensorData.temperature}°C</p>
                    </div>
                  </div>
                  <Badge variant="secondary" className="bg-green-100 text-green-700">正常</Badge>
                </div>
              </CardContent>
            </Card>

            {/* 湿度监测 */}
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="p-2 rounded-xl bg-blue-100">
                      <Droplets className="h-5 w-5 text-blue-600" />
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">湿度监测</p>
                      <p className="text-xl font-bold">{sensorData.humidity}% RH</p>
                    </div>
                  </div>
                  <Badge variant="secondary" className="bg-green-100 text-green-700">正常</Badge>
                </div>
              </CardContent>
            </Card>

            {/* 光照强度 */}
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="p-2 rounded-xl bg-amber-100">
                      <Sun className="h-5 w-5 text-amber-600" />
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">光照强度</p>
                      <p className="text-xl font-bold">{sensorData.light} lux</p>
                    </div>
                  </div>
                  <Badge variant="secondary" className="bg-green-100 text-green-700">适宜</Badge>
                </div>
              </CardContent>
            </Card>

            {/* 人体红外状态 */}
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="p-2 rounded-xl bg-purple-100">
                      <User className="h-5 w-5 text-purple-600" />
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">人体红外状态</p>
                      <p className="text-sm font-medium">
                        {sensorData.hasHuman ? "有人来查看植物" : "无人检测到"}
                      </p>
                    </div>
                  </div>
                  <Badge variant="secondary" className={sensorData.hasHuman ? "bg-purple-100 text-purple-700" : "bg-gray-100 text-gray-600"}>
                    {sensorData.hasHuman ? "检测到" : "无"}
                  </Badge>
                </div>
              </CardContent>
            </Card>

            {/* 异常提醒 */}
            <Card className={sensorData.isFallen ? "border-destructive/50 bg-destructive/5" : ""}>
              <CardContent className="p-4">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className={`p-2 rounded-xl ${sensorData.isFallen ? "bg-red-100" : "bg-gray-100"}`}>
                      <AlertTriangle className={`h-5 w-5 ${sensorData.isFallen ? "text-red-600" : "text-gray-500"}`} />
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">异常提醒</p>
                      <p className="text-sm font-medium">
                        {sensorData.isFallen ? "植物倒下，请检查！" : "一切正常"}
                      </p>
                    </div>
                  </div>
                  <Badge variant={sensorData.isFallen ? "destructive" : "secondary"}>
                    {sensorData.isFallen ? "警告" : "正常"}
                  </Badge>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </main>
    </div>
  )
}