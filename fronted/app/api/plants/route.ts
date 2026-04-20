import { NextRequest, NextResponse } from "next/server"

const BACKEND_BASE_URL = process.env.BACKEND_BASE_URL || "http://localhost:8080"

export async function GET(req: NextRequest) {
  try {
    const headers: HeadersInit = {}
    const authorization = req.headers.get("authorization")
    if (authorization) headers.Authorization = authorization

    const backendResponse = await fetch(`${BACKEND_BASE_URL}/plants`, {
      method: "GET",
      headers,
      cache: "no-store",
    })

    const responseText = await backendResponse.text()
    let data = responseText ? JSON.parse(responseText) : null
    return NextResponse.json(data, { status: backendResponse.status })
  } catch (error) {
    return NextResponse.json(
      { code: 500, message: error instanceof Error ? error.message : "server error", data: [] },
      { status: 500 }
    )
  }
}