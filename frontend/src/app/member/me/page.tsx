import { cookies } from "next/headers";
import ClientPage from "./ClientPage";

function parseAccessToken(accessToken: string | undefined) {
  let isAccessTokenExpired = true;
  let accessTokenPayload = null;

  if (accessToken) {
    try {
      const tokenParts = accessToken.split(".");
      accessTokenPayload = JSON.parse(
        Buffer.from(tokenParts[1], "base64").toString()
      );
      const expTimestamp = accessTokenPayload.exp * 1000;
      isAccessTokenExpired = Date.now() > expTimestamp;
    } catch (e) {
      console.error("토큰 파싱 중 오류 발생:", e);
    }
  }

  const isLogin =
    typeof accessTokenPayload === "object" && accessTokenPayload !== null;

  return { isLogin, isAccessTokenExpired, accessTokenPayload };
}

export default async function Page() {
  const cookieStore = await cookies();
  const accessToken = cookieStore.get("accessToken")?.value;

  const { accessTokenPayload } = parseAccessToken(accessToken);

  const me = {
    id: accessTokenPayload.id,
    createDate: "",
    modifyDate: "",
    nickname: "",
  };

  return <ClientPage me={me} />;
}
