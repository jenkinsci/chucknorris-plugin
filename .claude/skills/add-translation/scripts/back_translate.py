#!/usr/bin/env python3
"""Back-translate a Java .properties file via Google Translate and output comparison data.

Outputs JSON with each entry's original English, translated text, and Google Translate
back-translation. Does NOT judge quality - that's Claude's job. This script just
provides independent back-translation data for review.
"""

import json
import re
import sys
import time
import urllib.parse
import urllib.request


def parse_properties(filepath):
    """Parse a Java .properties file, decoding \\uXXXX escapes. Returns ordered list of (key, value)."""
    entries = []
    with open(filepath, "r") as f:
        for line in f:
            line = line.rstrip("\n")
            if not line or line.startswith("#") or line.startswith("!"):
                continue
            m = re.match(r"^([^=\\]*(?:\\.[^=\\]*)*)=(.*)", line)
            if not m:
                continue
            key = m.group(1).strip()
            value = m.group(2)
            entries.append((key, value))
    return entries


def decode_unicode_escapes(text):
    """Decode Java \\uXXXX escapes and backslash-escaped special chars to readable text."""
    decoded = re.sub(
        r"\\u([0-9a-fA-F]{4})", lambda m: chr(int(m.group(1), 16)), text
    )
    decoded = decoded.replace("\\=", "=")
    decoded = decoded.replace("\\:", ":")
    return decoded


def google_translate(text, src_lang, tgt_lang="en", retries=1):
    """Translate text via Google Translate API. Returns translated string or None on failure."""
    url = "https://translate.googleapis.com/translate_a/single"
    params = urllib.parse.urlencode(
        {"client": "gtx", "sl": src_lang, "tl": tgt_lang, "dt": "t", "q": text}
    )
    req = urllib.request.Request(f"{url}?{params}")
    req.add_header("User-Agent", "Mozilla/5.0")

    for attempt in range(retries + 1):
        try:
            resp = urllib.request.urlopen(req, timeout=10)
            data = json.loads(resp.read().decode("utf-8"))
            return "".join(part[0] for part in data[0] if part[0])
        except Exception as e:
            if attempt < retries:
                print(f"  Retry after error: {e}", file=sys.stderr)
                time.sleep(2)
            else:
                return None


def main():
    if len(sys.argv) < 4:
        print(
            "Usage: back_translate.py <translated_file> <source_locale> <base_english_file> [key1 key2 ...]",
            file=sys.stderr,
        )
        print(
            "  If keys are provided, only those entries are back-translated.",
            file=sys.stderr,
        )
        sys.exit(1)

    translated_file = sys.argv[1]
    source_locale = sys.argv[2]
    base_file = sys.argv[3]
    filter_keys = set(sys.argv[4:]) if len(sys.argv) > 4 else None

    translated_entries = parse_properties(translated_file)
    base_entries = parse_properties(base_file)

    base_dict = {k: v for k, v in base_entries}
    translated_dict = {k: v for k, v in translated_entries}

    # Skip metadata keys (values that are just numbers)
    skip_keys = {k for k, v in base_entries if v.strip().isdigit()}

    keys_to_check = [
        k
        for k, _ in base_entries
        if k in translated_dict and k not in skip_keys
    ]

    if filter_keys:
        keys_to_check = [k for k in keys_to_check if k in filter_keys]

    total = len(keys_to_check)
    results = []

    print(
        f"Back-translating {total} entries from '{source_locale}' to 'en'...",
        file=sys.stderr,
    )

    for i, key in enumerate(keys_to_check):
        original_en = decode_unicode_escapes(base_dict[key])
        translated_text = decode_unicode_escapes(translated_dict[key])

        print(f"  [{i + 1}/{total}] {key}", file=sys.stderr)

        back_translated = google_translate(translated_text, source_locale)
        time.sleep(0.2)

        results.append(
            {
                "key": key,
                "original_english": original_en,
                "translated": translated_text,
                "back_translated": back_translated or "ERROR: request failed",
            }
        )

    output = {"total": total, "locale": source_locale, "results": results}
    print(json.dumps(output, indent=2, ensure_ascii=False))
    print(f"\nDone: {total} entries back-translated.", file=sys.stderr)


if __name__ == "__main__":
    main()
