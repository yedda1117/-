import { NextRequest, NextResponse } from "next/server"

function generatePlantConfig(plantName: string) {
  const configs: Record<string, object> = {
    薄荷: {
      plantName: "薄荷",
      tempMin: 15, tempMax: 25,
      humidityMin: 50, humidityMax: 80,
      lightMin: 3000, lightMax: 25000,
      tempRiseSensitive: 4, humidityDropSensitive: 3, lightRiseSensitive: 2,
      careLevel: "简单",
      summary: "薄荷喜凉爽湿润环境，耐半阴，夏季需注意遮阳降温，保持土壤湿润。",
    },
    多肉: {
      plantName: "多肉",
      tempMin: 10, tempMax: 35,
      humidityMin: 20, humidityMax: 50,
      lightMin: 5000, lightMax: 40000,
      tempRiseSensitive: 1, humidityDropSensitive: 1, lightRiseSensitive: 3,
      careLevel: "简单",
      summary: "多肉植物耐旱耐晒，需充足光照，浇水遵循「干透浇透」原则，避免积水烂根。",
    },
    绿萝: {
      plantName: "绿萝",
      tempMin: 18, tempMax: 30,
      humidityMin: 60, humidityMax: 85,
      lightMin: 1000, lightMax: 15000,
      tempRiseSensitive: 2, humidityDropSensitive: 4, lightRiseSensitive: 1,
      careLevel: "简单",
      summary: "绿萝耐阴性强，适合室内养殖，喜高湿环境，避免阳光直射，定期喷水保湿。",
    },
    虎皮兰: {
      plantName: "虎皮兰",
      tempMin: 13, tempMax: 32,
      humidityMin: 30, humidityMax: 60,
      lightMin: 2000, lightMax: 30000,
      tempRiseSensitive: 1, humidityDropSensitive: 1, lightRiseSensitive: 2,
      careLevel: "简单",
      summary: "虎皮兰极耐旱耐阴，对环境适应性极强，是最易养护的室内植物之一，少浇水即可。",
    },
    绣球: {
      plantName: "绣球",
      tempMin: 16, tempMax: 26,
      humidityMin: 55, humidityMax: 80,
      lightMin: 4000, lightMax: 20000,
      tempRiseSensitive: 3, humidityDropSensitive: 4, lightRiseSensitive: 2,
      careLevel: "中等",
      summary: "绣球花喜凉爽湿润，需充足散射光，夏季避免强光直射，保持土壤湿润但不积水。",
    },
    向日葵: {
      plantName: "向日葵",
      tempMin: 18, tempMax: 35,
      humidityMin: 40, humidityMax: 70,
      lightMin: 8000, lightMax: 50000,
      tempRiseSensitive: 2, humidityDropSensitive: 2, lightRiseSensitive: 4,
      careLevel: "中等",
      summary: "向日葵喜阳光充足、温暖干燥的环境，需要充足的直射光照，适当控制浇水频率。",
    },
  }

  return (
    configs[plantName] ?? {
      plantName,
      tempMin: 18, tempMax: 28,
      humidityMin: 40, humidityMax: 70,
      lightMin: 2000, lightMax: 20000,
      tempRiseSensitive: 3, humidityDropSensitive: 2, lightRiseSensitive: 2,
      careLevel: "中等",
      summary: `${plantName}适应性较强，保持适宜的温湿度和光照即可健康生长，定期观察叶片状态。`,
    }
  )
}

export async function POST(req: NextRequest) {
  try {
    const body = await req.json()
    const { plantName } = body

    if (!plantName || typeof plantName !== "string") {
      return NextResponse.json({ code: 400, message: "plantName is required", data: null }, { status: 400 })
    }

    // Simulate AI processing delay
    await new Promise((resolve) => setTimeout(resolve, 1500))

    const data = generatePlantConfig(plantName.trim())

    return NextResponse.json({ code: 200, message: "success", data })
  } catch (error) {
    return NextResponse.json(
      { code: 500, message: error instanceof Error ? error.message : "Internal Server Error", data: null },
      { status: 500 },
    )
  }
}
