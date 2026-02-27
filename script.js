// ============================================================
//  DreamDoudou - Script
//  Pixel Art Style Interactions & Easter Eggs
// ============================================================

// ---- Soft copy/paste/selection lock ----
const block = (e) => e.preventDefault();
document.addEventListener("copy",        block);
document.addEventListener("cut",         block);
document.addEventListener("paste",       block);
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
document.addEventListener("gesturestart",  block, { passive: false });
document.addEventListener("gesturechange", block, { passive: false });
document.addEventListener("gestureend",    block, { passive: false });

// ============================================================
//  🪵 Wood Button Press Animation
// ============================================================
document.querySelectorAll(".wood-btn, .scroll-item, .lower-item").forEach(btn => {
    btn.addEventListener("pointerdown", () => {
        btn.classList.add("pressed");
    });
    btn.addEventListener("pointerup",    () => {
        setTimeout(() => btn.classList.remove("pressed"), 150);
    });
    btn.addEventListener("pointerleave", () => {
        btn.classList.remove("pressed");
    });
});

// ============================================================
//  🎊 パネル ホバー揺れ強化
// ============================================================
document.querySelectorAll(".sway-left, .sway-center, .sway-right").forEach(panel => {
    panel.addEventListener("mouseenter", () => {
        panel.style.animationPlayState = "running";
        panel.style.animationDuration  = "1.5s";
    });
    panel.addEventListener("mouseleave", () => {
        panel.style.animationDuration = panel.classList.contains("sway-center") ? "6s" :
                                        panel.classList.contains("sway-left")   ? "5s" : "4.5s";
    });
});

// ============================================================
//  🍌 Easter Egg: タイトル板を10回クリックでバナナ雨！
// ============================================================
let titleClickCount = 0;
let lastClickTime   = 0;
const RESET_DELAY   = 3000; // 3秒でリセット

const titleBoard = document.getElementById("title-board");
const bananaRain = document.getElementById("banana-rain");

if (titleBoard && bananaRain) {
    titleBoard.addEventListener("click", () => {
        const now = Date.now();
        if (now - lastClickTime > RESET_DELAY) {
            titleClickCount = 0;
        }
        lastClickTime = now;
        titleClickCount++;

        // カウントに応じた小さなフィードバック
        if (titleClickCount >= 3 && titleClickCount < 10) {
            titleBoard.style.filter = `hue-rotate(${titleClickCount * 10}deg)`;
        }

        if (titleClickCount >= 10) {
            titleClickCount = 0;
            titleBoard.style.filter = "";
            launchBananaRain();
        }
    });
}

function launchBananaRain() {
    const count = 40;
    for (let i = 0; i < count; i++) {
        const banana = document.createElement("div");
        banana.className = "banana-drop";
        banana.textContent = ["🍌", "🍋", "⭐", "🌟"][Math.floor(Math.random() * 4)];
        banana.style.left     = Math.random() * 100 + "vw";
        banana.style.fontSize = (1.2 + Math.random() * 1.8) + "rem";
        const dur = 2 + Math.random() * 3;
        banana.style.animationDuration = dur + "s";
        banana.style.animationDelay    = (Math.random() * 2) + "s";
        bananaRain.appendChild(banana);

        // アニメーション終了後に削除
        banana.addEventListener("animationend", () => banana.remove());
    }
}

// ============================================================
//  ✨ Easter Egg 2: コナミコマンド
// ============================================================
const KONAMI = ["ArrowUp","ArrowUp","ArrowDown","ArrowDown",
                "ArrowLeft","ArrowRight","ArrowLeft","ArrowRight","b","a"];
let konamiIdx = 0;

document.addEventListener("keydown", (e) => {
    if (e.key === KONAMI[konamiIdx]) {
        konamiIdx++;
        if (konamiIdx === KONAMI.length) {
            konamiIdx = 0;
            launchBananaRain();
            // さらにページ全体がピクセレート
            document.body.style.transition = "filter 0.3s step-end";
            document.body.style.filter = "saturate(3) hue-rotate(180deg)";
            setTimeout(() => {
                document.body.style.filter = "";
            }, 2000);
        }
    } else {
        konamiIdx = 0;
    }
});

// ============================================================
//  🔊 ボタンクリック音（Web Audio API で木の音っぽい！）
// ============================================================
let audioCtx = null;

function playWoodClick() {
    try {
        if (!audioCtx) audioCtx = new (window.AudioContext || window.webkitAudioContext)();
        const osc  = audioCtx.createOscillator();
        const gain = audioCtx.createGain();
        osc.connect(gain);
        gain.connect(audioCtx.destination);
        osc.type      = "square";
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
