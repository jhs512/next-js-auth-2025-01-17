"use client";

import client from "@/lib/backend/client";
import { useRouter } from "next/navigation";

export default function ClientPage() {
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const form = e.target as HTMLFormElement;

    if (form.username.value.length === 0) {
      alert("아이디를 입력해주세요.");
      form.username.focus();

      return;
    }

    if (form.password.value.length === 0) {
      alert("비밀번호를 입력해주세요.");
      form.password.focus();

      return;
    }

    if (form.passwordConfirm.value.length === 0) {
      alert("비밀번호 확인을 입력해주세요.");
      form.passwordConfirm.focus();

      return;
    }

    if (form.password.value != form.passwordConfirm.value) {
      alert("비밀번호가 일치하지 않습니다. 다시 입력해주세요.");
      form.password.focus();

      return;
    }

    if (form.nickname.value.length === 0) {
      alert("별명을 입력해주세요.");
      form.nickname.focus();

      return;
    }

    const response = await client.POST("/api/v1/members/join", {
      body: {
        username: form.username.value,
        password: form.password.value,
        nickname: form.nickname.value,
      },
    });

    if (response.error) {
      alert(response.error.msg);
      return;
    }

    alert(response.data.msg);

    router.replace("/member/login");
  };

  return (
    <>
      <h1 className="text-2xl font-bold">Login 페이지</h1>
      <form onSubmit={handleSubmit}>
        <div>
          <label>아이디</label>
          <input
            type="text"
            name="username"
            className="p-2"
            placeholder="아이디"
          />
        </div>
        <div>
          <label>비밀번호</label>
          <input
            type="password"
            name="password"
            className="p-2"
            placeholder="비밀번호"
          />
        </div>
        <div>
          <label>비밀번호 확인</label>
          <input
            type="password"
            name="passwordConfirm"
            className="p-2"
            placeholder="비밀번호 확인"
          />
        </div>
        <div>
          <label>별명</label>
          <input
            type="text"
            name="nickname"
            className="p-2"
            placeholder="별명"
          />
        </div>
        <div>
          <input type="submit" value="회원가입" />
        </div>
      </form>
    </>
  );
}
