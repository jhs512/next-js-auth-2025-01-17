"use client";

import client from "@/lib/backend/client";
import { useRouter } from "next/navigation";

export default function ClientPage() {
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const form = e.target as HTMLFormElement;

    if (form._title.value.length === 0) {
      alert("제목을 입력해주세요.");
      form._title.focus();

      return;
    }

    if (form.content.value.length === 0) {
      alert("내용을 입력해주세요.");
      form.content.focus();

      return;
    }

    const response = await client.POST("/api/v1/posts", {
      body: {
        title: form._title.value,
        content: form.content.value,
        listed: form.listed.checked,
        published: form.published.checked,
      },
    });

    if (response.error) {
      alert(response.error.msg);
      return;
    }

    alert(response.data.msg);

    router.replace(`/post/${response.data.data.id}`);
  };

  return (
    <>
      <h1>글 작성</h1>
      <form onSubmit={handleSubmit}>
        <div>
          <label>제목</label>
          <input
            type="text"
            name="_title"
            className="p-2"
            placeholder="제목"
            autoFocus
          />
        </div>
        <div>
          <label>내용</label>
          <textarea name="content" className="p-2" placeholder="내용" />
        </div>
        <div>
          <label>검색</label>
          <input type="checkbox" name="listed" />
        </div>
        <div>
          <label>공개</label>
          <input type="checkbox" name="published" />
        </div>
        <div>
          <input type="submit" value="작성" />
        </div>
      </form>
    </>
  );
}
