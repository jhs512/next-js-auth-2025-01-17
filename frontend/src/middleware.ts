import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { cookies } from "next/headers";
import client from "./lib/backend/client";
import { ResponseCookie } from "next/dist/compiled/@edge-runtime/cookies";

export async function middleware(req: NextRequest) {
  const accessToken = (await cookies()).get("accessToken")?.value;

  let isExpired = true;

  if (accessToken) {
    try {
      const tokenParts = accessToken.split(".");
      const payload = JSON.parse(
        Buffer.from(tokenParts[1], "base64").toString()
      );
      const expTimestamp = payload.exp * 1000; // exp는 초 단위이므로 밀리초로 변환
      isExpired = Date.now() > expTimestamp;
    } catch (e) {
      console.error("토큰 파싱 중 오류 발생:", e);
    }
  }

  console.log(`isExpired: ${isExpired}`);

  const meResponse = await client.GET("/api/v1/members/me", {
    headers: {
      cookie: (await cookies()).toString(),
    },
  });

  if (meResponse.response.headers.get("Set-Cookie")) {
    const meResponseCookies = meResponse.response.headers
      .get("Set-Cookie")
      ?.split(",");

    if (meResponseCookies) {
      for (const cookieStr of meResponseCookies) {
        // 쿠키 문자열을 각 속성으로 파싱
        const parts = cookieStr.split(";").map((p) => p.trim());
        const [name, value] = parts[0].split("=");

        // accessToken 또는 apiKey 쿠키만 처리
        if (name !== "accessToken" && name !== "apiKey") continue;

        const options: Partial<ResponseCookie> = {};
        for (const part of parts.slice(1)) {
          if (part.toLowerCase() === "httponly") options.httpOnly = true;
          else if (part.toLowerCase() === "secure") options.secure = true;
          else {
            const [key, val] = part.split("=");
            const keyLower = key.toLowerCase();
            if (keyLower === "domain") options.domain = val;
            else if (keyLower === "path") options.path = val;
            else if (keyLower === "max-age") options.maxAge = parseInt(val);
            else if (keyLower === "expires")
              options.expires = new Date(val).getTime();
            else if (keyLower === "samesite")
              options.sameSite = val.toLowerCase() as "lax" | "strict" | "none";
          }
        }

        (await cookies()).set(name, value, options);
      }
    }
  }

  const isLogin = meResponse.data !== undefined;

  if (
    (req.nextUrl.pathname.startsWith("/post/write") ||
      req.nextUrl.pathname.match(/^\/post\/\d+\/edit$/)) &&
    !isLogin
  ) {
    return new NextResponse("로그인이 필요합니다.", {
      status: 401,
      headers: {
        "Content-Type": "text/html; charset=utf-8",
      },
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
