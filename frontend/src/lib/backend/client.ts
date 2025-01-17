import type { paths } from "@/lib/backend/apiV1/schema";
import createClient from "openapi-fetch";

const client = createClient<paths>({
  baseUrl: "http://localhost:8080",
  credentials: "include",
});

export default client;
