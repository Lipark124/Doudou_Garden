// ---- Soft copy/paste/selection lock ----
const block = (e) => e.preventDefault();
document.addEventListener("copy", block);
document.addEventListener("cut", block);
document.addEventListener("paste", block);
document.addEventListener("contextmenu", block);
document.addEventListener("selectstart", block);

// ---- Zoom lock ----
document.addEventListener("keydown", (e) => {
    const isCmd = e.ctrlKey || e.metaKey;
    if (!isCmd) return;
    const k = e.key.toLowerCase();
    if (k === "+" || k === "-" || k === "=" || k === "0") e.preventDefault();
}, { passive: false });
document.addEventListener("wheel", (e) => {
    if (e.ctrlKey || e.metaKey) e.preventDefault();
}, { passive: false });
document.addEventListener("gesturestart", block, { passive: false });
document.addEventListener("gesturechange", block, { passive: false });
document.addEventListener("gestureend", block, { passive: false });

// ----Button Press Animation ----
document.querySelectorAll(".wood-btn, .scroll-item, .lower-item").forEach(btn => {
    btn.addEventListener("pointerdown", () => {
        btn.classList.add("pressed");
    });
    btn.addEventListener("pointerup", () => {
        setTimeout(() => btn.classList.remove("pressed"), 150);
    });
    btn.addEventListener("pointerleave", () => {
        btn.classList.remove("pressed");
    });
});

// ----Button Click Sound----
let audioCtx = null;

function playWoodClick() {
    try {
        if (!audioCtx) audioCtx = new (window.AudioContext || window.webkitAudioContext)();
        const osc = audioCtx.createOscillator();
        const gain = audioCtx.createGain();
        osc.connect(gain);
        gain.connect(audioCtx.destination);
        osc.type = "square";
        osc.frequency.setValueAtTime(220, audioCtx.currentTime);
        osc.frequency.exponentialRampToValueAtTime(80, audioCtx.currentTime + 0.08);
        gain.gain.setValueAtTime(0.15, audioCtx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.001, audioCtx.currentTime + 0.1);
        osc.start(audioCtx.currentTime);
        osc.stop(audioCtx.currentTime + 0.1);
    } catch (_) { /* silently ignore */ }
}

document.querySelectorAll(".wood-btn").forEach(btn => {
    btn.addEventListener("click", playWoodClick);
});

// ----Panel Toggle----
document.querySelectorAll('.panel-toggle').forEach(btn => {
    btn.addEventListener('click', () => {
        const targetId = btn.getAttribute('aria-controls');
        const panel = document.getElementById(targetId);
        if (!panel) return;

        const isCollapsed = panel.classList.toggle('collapsed');
        btn.setAttribute('aria-expanded', !isCollapsed);

        playWoodClick();
    });
});
