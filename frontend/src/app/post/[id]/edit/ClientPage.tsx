"use client";

import { components } from "@/lib/backend/apiV1/schema";
import client from "@/lib/backend/client";
import { useRouter } from "next/navigation";

export default function ClientPage({
  post,
}: {
  post: components["schemas"]["PostWithContentDto"];
}) {
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

    const response = await client.PUT("/api/v1/posts/{id}", {
      params: {
        path: {
          id: post.id,
        },
      },
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
      <h1>글 수정</h1>

      <div>
        <button onClick={() => router.back()}>뒤로가기</button>
        <h2>{post.id}번 게시물 수정</h2>

        <form onSubmit={handleSubmit}>
          <div>
            <label>작성날짜</label>
            <div>{post.createDate}</div>
          </div>

          <div>
            <label>수정날짜</label>
            <div>{post.modifyDate}</div>
          </div>

          <div>
            <label>제목</label>
            <input
              type="text"
              name="_title"
              className="p-2"
              placeholder="제목"
              defaultValue={post.title}
            />
          </div>

          <div>
            <label>내용</label>
            <textarea
              name="content"
              className="p-2"
              placeholder="내용"
              defaultValue={post.content}
              autoFocus
            />
          </div>

          <div>
            <label>검색</label>
            <input type="checkbox" name="listed" defaultChecked={post.listed} />
          </div>

          <div>
            <label>공개</label>
            <input
              type="checkbox"
              name="published"
              defaultChecked={post.published}
            />
          </div>

          <div>
            <input type="submit" value="수정" />
          </div>
        </form>
      </div>
    </>
  );
}
