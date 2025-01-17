"use client";

import client from "@/lib/backend/client";

export default function ClientPage() {
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

    const response = await client.POST("/api/v1/members/login", {
      body: {
        username: form.username.value,
        password: form.password.value,
      },
    });

    if (response.error) {
      alert(response.error.msg);
      return;
    }

    alert(response.data.msg);

    window.location.replace("/");
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
          <input type="submit" value="로그인" />
        </div>
      </form>
    </>
  );
}
