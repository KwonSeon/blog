import { Container } from "@/src/shared/ui";
import { ProjectEditor } from "@/src/widgets/project-editor";
import { editorToken } from "@/src/shared/lib/auth/session";
import { redirect } from "next/navigation";
export default async function NewProject() {
  if (!(await editorToken())) redirect("/admin/login");
  return (
    <Container size="narrow" className="py-12">
      <h1 className="text-3xl font-semibold mb-8">프로젝트 추가</h1>
      <ProjectEditor />
    </Container>
  );
}
