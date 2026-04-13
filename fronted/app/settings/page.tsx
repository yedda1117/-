"use client"

import { NavHeader } from "@/components/nav-header"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Switch } from "@/components/ui/switch"
import { Slider } from "@/components/ui/slider"
import { Badge } from "@/components/ui/badge"
import { Separator } from "@/components/ui/separator"
import {
  Wifi,
  Thermometer,
  Droplets,
  Sun,
  Bell,
  Shield,
  Smartphone,
  RefreshCw,
  Save,
  CheckCircle,
} from "lucide-react"

export default function SettingsPage() {
  return (
    <div className="min-h-screen bg-background">
      <NavHeader />

      <main className="container mx-auto px-6 py-8 max-w-4xl">
        <h1 className="text-2xl font-bold mb-6">系统设置</h1>

        <div className="space-y-6">
          {/* 设备连接状态 */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Wifi className="h-5 w-5 text-primary" />
                设备连接状态
              </CardTitle>
              <CardDescription>管理与小熊派开发板的连接</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between p-4 rounded-xl bg-green-50 border border-green-200">
                <div className="flex items-center gap-3">
                  <CheckCircle className="h-5 w-5 text-green-600" />
                  <div>
                    <p className="font-medium">小熊派开发板</p>
                    <p className="text-sm text-muted-foreground">MQTT 连接正常 | 延时: 23ms</p>
                  </div>
                </div>
                <Badge className="bg-green-100 text-green-700">已连接</Badge>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm text-muted-foreground">MQTT 服务器地址</label>
                  <Input value="mqtt.example.com" className="mt-1" readOnly />
                </div>
                <div>
                  <label className="text-sm text-muted-foreground">端口</label>
                  <Input value="1883" className="mt-1" readOnly />
                </div>
              </div>

              <Button variant="outline" className="w-full">
                <RefreshCw className="h-4 w-4 mr-2" />
                重新连接设备
              </Button>
            </CardContent>
          </Card>

          {/* 环境阈值设置 */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Thermometer className="h-5 w-5 text-primary" />
                环境阈值设置
              </CardTitle>
              <CardDescription>设置环境参数的警报阈值</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              {/* 温度阈值 */}
              <div className="space-y-3">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <Thermometer className="h-4 w-4 text-orange-500" />
                    <span className="font-medium">温度范围</span>
                  </div>
                  <span className="text-sm text-muted-foreground">18°C - 30°C</span>
                </div>
                <Slider defaultValue={[18, 30]} min={0} max={50} step={1} />
                <div className="flex justify-between text-xs text-muted-foreground">
                  <span>0°C</span>
                  <span>50°C</span>
                </div>
              </div>

              <Separator />

              {/* 湿度阈值 */}
              <div className="space-y-3">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <Droplets className="h-4 w-4 text-blue-500" />
                    <span className="font-medium">湿度范围</span>
                  </div>
                  <span className="text-sm text-muted-foreground">40% - 80%</span>
                </div>
                <Slider defaultValue={[40, 80]} min={0} max={100} step={1} />
                <div className="flex justify-between text-xs text-muted-foreground">
                  <span>0%</span>
                  <span>100%</span>
                </div>
              </div>

              <Separator />

              {/* 光照阈值 */}
              <div className="space-y-3">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <Sun className="h-4 w-4 text-amber-500" />
                    <span className="font-medium">光照范围</span>
                  </div>
                  <span className="text-sm text-muted-foreground">300 - 30,000 lux</span>
                </div>
                <Slider defaultValue={[300, 30000]} min={0} max={50000} step={100} />
                <div className="flex justify-between text-xs text-muted-foreground">
                  <span>0 lux</span>
                  <span>50,000 lux</span>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* 通知设置 */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Bell className="h-5 w-5 text-primary" />
                通知设置
              </CardTitle>
              <CardDescription>配置系统通知与警报</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between p-4 rounded-xl bg-muted/50">
                <div>
                  <p className="font-medium">温度异常通知</p>
                  <p className="text-sm text-muted-foreground">温度超出范围时发送通知</p>
                </div>
                <Switch defaultChecked />
              </div>
              <div className="flex items-center justify-between p-4 rounded-xl bg-muted/50">
                <div>
                  <p className="font-medium">湿度异常通知</p>
                  <p className="text-sm text-muted-foreground">湿度超出范围时发送通知</p>
                </div>
                <Switch defaultChecked />
              </div>
              <div className="flex items-center justify-between p-4 rounded-xl bg-muted/50">
                <div>
                  <p className="font-medium">光照异常通知</p>
                  <p className="text-sm text-muted-foreground">光照超出范围时发送通知</p>
                </div>
                <Switch defaultChecked />
              </div>
              <div className="flex items-center justify-between p-4 rounded-xl bg-muted/50">
                <div>
                  <p className="font-medium">植物位置异常</p>
                  <p className="text-sm text-muted-foreground">检测到花盆倾斜或倒下时通知</p>
                </div>
                <Switch defaultChecked />
              </div>
              <div className="flex items-center justify-between p-4 rounded-xl bg-muted/50">
                <div>
                  <p className="font-medium">有人来访通知</p>
                  <p className="text-sm text-muted-foreground">人体红外检测到有人时通知</p>
                </div>
                <Switch />
              </div>
            </CardContent>
          </Card>

          {/* 自动控制设置 */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Shield className="h-5 w-5 text-primary" />
                自动控制
              </CardTitle>
              <CardDescription>配置设备自动控制策略</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between p-4 rounded-xl bg-muted/50">
                <div>
                  <p className="font-medium">自动补光</p>
                  <p className="text-sm text-muted-foreground">光照低于阈值时自动开启补光灯</p>
                </div>
                <Switch defaultChecked />
              </div>
              <div className="flex items-center justify-between p-4 rounded-xl bg-muted/50">
                <div>
                  <p className="font-medium">自动散热</p>
                  <p className="text-sm text-muted-foreground">温度高于阈值时自动开启风扇</p>
                </div>
                <Switch defaultChecked />
              </div>
            </CardContent>
          </Card>

          {/* 系统信息 */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Smartphone className="h-5 w-5 text-primary" />
                系统信息
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div className="p-3 rounded-xl bg-muted/50">
                  <p className="text-muted-foreground">系统版本</p>
                  <p className="font-medium">v1.0.0</p>
                </div>
                <div className="p-3 rounded-xl bg-muted/50">
                  <p className="text-muted-foreground">硬件版本</p>
                  <p className="font-medium">BearPi-HM Nano</p>
                </div>
                <div className="p-3 rounded-xl bg-muted/50">
                  <p className="text-muted-foreground">固件版本</p>
                  <p className="font-medium">HarmonyOS 3.0</p>
                </div>
                <div className="p-3 rounded-xl bg-muted/50">
                  <p className="text-muted-foreground">最后同步</p>
                  <p className="font-medium">2026-04-10 16:45</p>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* 保存按钮 */}
          <Button className="w-full" size="lg">
            <Save className="h-4 w-4 mr-2" />
            保存设置
          </Button>
        </div>
      </main>
    </div>
  )
}
