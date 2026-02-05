// ---- copy/paste/selection lock (soft lock) ----
const block = (e) => e.preventDefault();

// 文章の選択・コピー系をブロック
document.addEventListener("copy", block);
document.addEventListener("cut", block);
document.addEventListener("paste", block);
document.addEventListener("contextmenu", block);
document.addEventListener("selectstart", block);

// ---- zoom lock (soft lock) ----
// Ctrl/⌘ + +/-/0 , Ctrl/⌘ + wheel を抑止
document.addEventListener("keydown", (e) => {
    const isCmd = e.ctrlKey || e.metaKey;
    if (!isCmd) return;
    const k = e.key.toLowerCase();
    if (k === "+" || k === "-" || k === "=" || k === "0") e.preventDefault();
}, { passive: false });

document.addEventListener("wheel", (e) => {
    if (e.ctrlKey || e.metaKey) e.preventDefault();
}, { passive: false });

// iOS系のピンチズーム抑止（効く時と効かない時がある）
document.addEventListener("gesturestart", block, { passive: false });
document.addEventListener("gesturechange", block, { passive: false });
document.addEventListener("gestureend", block, { passive: false });