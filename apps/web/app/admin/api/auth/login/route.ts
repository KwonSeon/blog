export async function POST() {
  return Response.json(
    { message: "통합 로그인 화면을 사용해 주세요." },
    { status: 410 },
  );
}
