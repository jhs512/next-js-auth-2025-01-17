import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { cookies } from "next/headers";
import client from "./lib/backend/client";

export async function middleware(req: NextRequest) {
  const meResponse = await client.GET("/api/v1/members/me", {
    headers: {
      cookie: (await cookies()).toString(),
    },
  });

  if (meResponse.response.headers.get("Set-Cookie")) {
    const cookieValue = meResponse.response.headers.get("Set-Cookie")!;

    console.log(`cookieValue: ${cookieValue}`);

    (await cookies()).set({
      name: cookieValue.split("=")[0],
      value: cookieValue.split("=")[1].split(";")[0],
    });
  }

  return NextResponse.next({
    headers: {
      cookie: (await cookies()).toString(),
    },
  });
}

export const config = {
  // 아래 2가지 경우에는 middleware를 실행하지 않도록 세팅
  // api 로 시작하거나 하는 요청 : /api/~~~
  // 정적 파일 요청 : /~~~.jpg, /~~~.png, /~~~.css, /~~~.js
  // PS. 여기서 말하는 api 로 시작하는 요청은 백엔드 API 서버로의 요청이 아니라 Next.js 고유의 API 서버로의 요청이다.
  // PS. 우리는 현재 이 기능을 사용하고 있지 않다.
  matcher: "/((?!.*\\.|api\\/).*)",
};
