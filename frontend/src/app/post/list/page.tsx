import client from "@/lib/backend/client";

import { cookies } from "next/headers";
import ClientPage from "./ClientPage";

export default async function Page({
  searchParams,
}: {
  searchParams: {
    searchKeywordType?: "title" | "content";
    searchKeyword?: string;
    pageSize?: number;
    page?: number;
  };
}) {
  const {
    searchKeyword = "",
    searchKeywordType = "title",
    pageSize = 10,
    page = 1,
  } = await searchParams;

  const response = await client.GET("/api/v1/posts", {
    params: {
      query: {
        searchKeyword,
        searchKeywordType,
        pageSize,
        page,
      },
    },
    headers: {
      cookie: (await cookies()).toString(),
    },
  });

  const responseBody = response.data!!;

  return (
    <>
      <ClientPage
        searchKeyword={searchKeyword}
        searchKeywordType={searchKeywordType}
        page={page}
        pageSize={pageSize}
        responseBody={responseBody}
      />
    </>
  );
}
