"use client";

import { components } from "@/lib/backend/apiV1/schema";
import client from "@/lib/backend/client";
import Link from "next/link";
import { useRouter } from "next/navigation";

export default function ClientPage({
  post,
}: {
  post: components["schemas"]["PostWithContentDto"];
}) {
  const router = useRouter();

  const handleDelete = async () => {
    if (!confirm("정말로 삭제하시겠습니까?")) return;

    const response = await client.DELETE("/api/v1/posts/{id}", {
      params: {
        path: {
          id: post.id,
        },
      },
    });

    if (response.error) {
      alert(response.error.msg);
      return;
    }

    alert(response.data.msg);

    router.replace("/post/list");
  };

  return (
    <div>
      <button onClick={() => router.back()}>뒤로가기</button>
      <hr />
      {post.id}번 게시물 상세페이지
      <hr />
      작성날짜 : {post.createDate}
      <hr />
      수정날짜 : {post.modifyDate}
      <hr />
      <h1>{post.title}</h1>
      <hr />
      <p>{post.content}</p>
      <div>
        {post.actorCanModify && (
          <Link href={`/post/${post.id}/edit`}>수정</Link>
        )}

        {post.actorCanDelete && <button onClick={handleDelete}>삭제</button>}
      </div>
    </div>
  );
}
