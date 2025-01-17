"use client";

import type { components } from "@/lib/backend/apiV1/schema";
import Link from "next/link";
import { useRouter } from "next/navigation";

export default function ClientPage({
  searchKeyword,
  searchKeywordType,
  page,
  pageSize,
  responseBody,
}: {
  searchKeyword: string;
  searchKeywordType: string;
  page: number;
  pageSize: number;
  responseBody: components["schemas"]["PageDtoPostDto"];
}) {
  const router = useRouter();

  return (
    <div>
      <form
        onSubmit={(e) => {
          e.preventDefault();

          const formData = new FormData(e.target as HTMLFormElement);
          const searchKeyword = formData.get("searchKeyword") as string;
          const searchKeywordType = formData.get("searchKeywordType") as string;
          const page = formData.get("page") as string;
          const pageSize = formData.get("pageSize") as string;

          router.push(
            `?page=${page}&pageSize=${pageSize}&searchKeywordType=${searchKeywordType}&searchKeyword=${searchKeyword}`
          );
        }}
      >
        <input type="hidden" name="page" value="1" />
        <select name="pageSize" defaultValue={pageSize}>
          <option disabled>페이당 행 수</option>
          <option value="10">10</option>
          <option value="30">30</option>
          <option value="50">50</option>
        </select>
        <select name="searchKeywordType" defaultValue={searchKeywordType}>
          <option disabled>검색어 타입</option>
          <option value="title">제목</option>
          <option value="content">내용</option>
        </select>
        <input
          placeholder="검색어를 입력해주세요."
          type="text"
          name="searchKeyword"
          defaultValue={searchKeyword}
        />
        <button type="submit">검색</button>
      </form>

      <div>
        <div>currentPageNumber: {responseBody.currentPageNumber}</div>

        <div>pageSize: {responseBody.pageSize}</div>

        <div>totalPages: {responseBody.totalPages}</div>

        <div>totalItems: {responseBody.totalItems}</div>
      </div>

      <hr />

      <div className="flex my-2 gap-2">
        {Array.from({ length: responseBody.totalPages }, (_, i) => i + 1).map(
          (pageNum) => (
            <Link
              key={pageNum}
              className={`px-2 py-1 border rounded ${
                pageNum === responseBody.currentPageNumber ? "text-red-500" : ""
              }`}
              href={`?page=${pageNum}&pageSize=${pageSize}&searchKeywordType=${searchKeywordType}&searchKeyword=${searchKeyword}`}
            >
              {pageNum}
            </Link>
          )
        )}
      </div>

      <hr />

      <ul>
        {responseBody.items.map((item) => (
          <li key={item.id} className="border-[2px] border-[red] my-3">
            <Link className="block" href={`/post/${item.id}`}>
              <div>id : {item.id}</div>
              <div>createDate : {item.createDate}</div>
              <div>modifyDate : {item.modifyDate}</div>
              <div>authorId : {item.authorId}</div>
              <div>authorName : {item.authorName}</div>
              <div>title : {item.title}</div>
              <div>published : {`${item.published}`}</div>
              <div>listed : {`${item.listed}`}</div>
            </Link>
          </li>
        ))}
      </ul>

      <hr />

      <div className="flex my-2 gap-2">
        {Array.from({ length: responseBody.totalPages }, (_, i) => i + 1).map(
          (pageNum) => (
            <Link
              key={pageNum}
              className={`px-2 py-1 border rounded ${
                pageNum === responseBody.currentPageNumber ? "text-red-500" : ""
              }`}
              href={`?page=${pageNum}&pageSize=${pageSize}&searchKeywordType=${searchKeywordType}&searchKeyword=${searchKeyword}`}
            >
              {pageNum}
            </Link>
          )
        )}
      </div>
    </div>
  );
}
