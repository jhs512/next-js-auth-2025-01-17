"use client";

import { components } from "@/lib/backend/apiV1/schema";
import client from "@/lib/backend/client";
import Link from "next/link";

export default function ClientLayout({
  children,
  me,
}: Readonly<{
  children: React.ReactNode;
  me: components["schemas"]["MemberDto"];
}>) {
  const isLogin = me.id !== 0;

  const logout = async () => {
    const response = await client.DELETE("/api/v1/members/logout");

    if (response.error) {
      alert(response.error.msg);
      return;
    }

    window.location.replace("/");
  };

  return (
    <>
      <header className="border-[2px] border-[red] p-5">
        <div className="flex gap-2">
          <Link href="/">홈</Link>
          <Link href="/about">소개</Link>
          <Link href="/post/list">글</Link>
          {isLogin && <Link href="/post/write">글 쓰기</Link>}
          {!isLogin && <Link href="/member/join">회원가입</Link>}
          {!isLogin && <Link href="/member/login">로그인</Link>}
          {isLogin && <button onClick={logout}>로그아웃</button>}
          {isLogin && <Link href="/member/me">내 정보</Link>}
        </div>
      </header>

      <main className="flex-grow border-[2px] border-[blue] p-5">
        {children}
      </main>

      <footer className="border-[2px] border-[pink] p-5">
        Copyright 2025.
      </footer>
    </>
  );
}
