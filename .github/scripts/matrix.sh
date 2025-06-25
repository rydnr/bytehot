#!/usr/bin/env sh

cat <<EOF
        // Matrix rain effect (lighter for documentation pages)
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        document.querySelector('.matrix-bg').appendChild(canvas);
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
        const matrix = "BYTEHOT0123456789";
        const drops = [];
        for(let x = 0; x < canvas.width / 15; x++) { drops[x] = 1; }
        function draw() {
            ctx.fillStyle = 'rgba(15, 15, 35, 0.05)';
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            ctx.fillStyle = '#00ff00';
            ctx.font = '12px Courier New';
            for(let i = 0; i < drops.length; i++) {
                const text = matrix[Math.floor(Math.random() * matrix.length)];
                ctx.fillText(text, i * 15, drops[i] * 15);
                if(drops[i] * 15 > canvas.height && Math.random() > 0.98) { drops[i] = 0; }
                drops[i]++;
            }
        }
        setInterval(draw, 50);
        window.addEventListener('resize', () => { canvas.width = window.innerWidth; canvas.height = window.innerHeight; });
EOF
