import test from "node:test";
import { execFileSync } from "node:child_process";
import { fileURLToPath } from "node:url";

test("repository operations regression suite", () => {
  execFileSync("python3", ["-m", "unittest", "discover", "-s", "tools/tests", "-v"], {
    cwd: fileURLToPath(new URL("../../../", import.meta.url)),
    timeout: 30000,
    stdio: "pipe",
  });
});
