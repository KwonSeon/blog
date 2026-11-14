"use client";
import { useRef, useState } from "react";
import {
  FormField,
  FormTextarea,
  FormMessage,
  SurfaceCard,
} from "@/src/shared/ui";
import { ActionButton } from "@/src/shared/ui/action-button";
import { PostMarkdown } from "@/src/entities/post";
import {
  requestAdminMediaUploadPresign,
  uploadFileToPresignedUrl,
  completeAdminMediaUpload,
} from "@/src/shared/lib/api/admin-media-api";
export function ProjectStoryEditor({
  projectId,
  initialContent,
}: {
  projectId: string;
  initialContent: string;
}) {
  const [content, setContent] = useState(initialContent),
    [caption, setCaption] = useState(""),
    [busy, setBusy] = useState(false),
    [message, setMessage] = useState(""),
    [error, setError] = useState("");
  const input = useRef<HTMLInputElement>(null);
  async function upload() {
    const file = input.current?.files?.[0];
    if (!file) {
      setError("소개에 넣을 이미지를 선택해 주세요.");
      return;
    }
    setBusy(true);
    setError("");
    setMessage("");
    try {
      const presign = await requestAdminMediaUploadPresign({
        namespace: "project-inline",
        originalFilename: file.name,
        contentType: file.type,
        sizeBytes: file.size,
      });
      await uploadFileToPresignedUrl({
        uploadUrl: presign.uploadUrl,
        file,
        contentType: file.type,
      });
      const asset = await completeAdminMediaUpload({
        mediaAssetId: presign.mediaAssetId,
        objectKey: presign.objectKey,
        originalFilename: file.name,
      });
      const alt = (caption || "프로젝트 화면").replace(/[\[\]\n\r]/g, " ");
      setContent(
        (s) =>
          s +
          `\n\n![${alt}](/media/assets/${asset.mediaAssetId}/content)\n\n${alt}\n`,
      );
      setMessage("이미지를 삽입했습니다. 상세 소개를 저장해 주세요.");
    } catch (e) {
      setError(e instanceof Error ? e.message : "업로드 실패");
    } finally {
      setBusy(false);
    }
  }
  async function save() {
    setBusy(true);
    setError("");
    setMessage("");
    try {
      const r = await fetch(`/admin/api/projects/${projectId}/story`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ contentMd: content }),
      });
      if (!r.ok) throw new Error("상세 소개를 저장하지 못했습니다.");
      setMessage("상세 소개가 저장되었습니다.");
    } catch (e) {
      setError(e instanceof Error ? e.message : "저장 실패");
    } finally {
      setBusy(false);
    }
  }
  return (
    <SurfaceCard padding="lg" className="mt-8">
      <h2 className="text-2xl font-semibold">서비스와 기능 소개</h2>
      <p className="mt-3 mb-6 text-muted-foreground leading-7">
        저장소를 공개하지 않아도 서비스의 목적, 주요 기능과 사용 방법을 소개할
        수 있습니다. 이미지 주소는 공개되므로 공개해도 되는 화면만 올려주세요.
      </p>
      <div className="grid gap-5">
        <FormTextarea
          label="상세 소개 (Markdown)"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          rows={14}
          maxLength={100000}
          disabled={busy}
          placeholder="## 어떤 서비스인가요?"
        />
        <FormField
          label="이미지 설명"
          value={caption}
          onChange={(e) => setCaption(e.target.value)}
          disabled={busy}
        />
        <label className="grid gap-2">
          기능 소개 이미지
          <input
            ref={input}
            type="file"
            accept="image/jpeg,image/png,image/webp,image/gif"
            disabled={busy}
          />
        </label>
        <div className="flex flex-wrap gap-3">
          <ActionButton onClick={() => void upload()} disabled={busy}>
            이미지 업로드·삽입
          </ActionButton>
          <ActionButton onClick={() => void save()} disabled={busy}>
            {busy ? "처리 중…" : "상세 소개 저장"}
          </ActionButton>
        </div>
        {error && <FormMessage variant="danger">{error}</FormMessage>}
        {message && <FormMessage variant="success">{message}</FormMessage>}
        <details open className="border-t border-border pt-6">
          <summary className="cursor-pointer font-semibold">
            방문자 화면 미리보기
          </summary>
          <PostMarkdown content={content} className="mt-6" />
        </details>
      </div>
    </SurfaceCard>
  );
}
