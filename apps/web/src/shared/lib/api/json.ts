import { parse, isLosslessNumber } from "lossless-json";
// Snowflake IDs exceed Number.MAX_SAFE_INTEGER. Preserve them before parsing rounds them.
export function parseApiJson(text: string): unknown {
  return parse(text, (key, value) =>
    isLosslessNumber(value)
      ? /Id$/.test(key)
        ? value.value
        : Number(value.value)
      : value,
  );
}
