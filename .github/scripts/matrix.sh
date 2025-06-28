#!/usr/bin/env sh

cat <<EOF
        // Enhanced Matrix rain effect - Red/Blue Pill metaphor
        // Red = ByteHot (freedom from restart tyranny)
        // Blue = Traditional development (restart slavery)
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        document.querySelector('.matrix-bg').appendChild(canvas);
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
        
        // Character sets representing the two worlds
        const redPillChars = "BYTEHOT🔥⚡🚀💡✨🔧⭐🎯"; // ByteHot symbols (freedom)
        const bluePillChars = "RESTART💤😴🐌⏳❌🔄📱💔"; // Traditional dev symbols (tyranny)
        const neutralChars = "0123456789ABCDEF";
        
        // Drop configuration
        const drops = [];
        const dropColors = [];
        const dropTypes = []; // 0=blue, 1=red, 2=awakening
        const awakeningTimer = [];
        
        // Initialize drops
        for(let x = 0; x < canvas.width / 15; x++) { 
            drops[x] = 1;
            dropColors[x] = Math.random() < 0.7 ? 'blue' : 'red'; // 70% blue (most are trapped)
            dropTypes[x] = dropColors[x] === 'blue' ? 0 : 1;
            awakeningTimer[x] = 0;
        }
        
        function getCharacter(type) {
            switch(type) {
                case 0: return bluePillChars[Math.floor(Math.random() * bluePillChars.length)];
                case 1: return redPillChars[Math.floor(Math.random() * redPillChars.length)];
                case 2: return neutralChars[Math.floor(Math.random() * neutralChars.length)];
                default: return neutralChars[Math.floor(Math.random() * neutralChars.length)];
            }
        }
        
        function getColor(type, timer) {
            switch(type) {
                case 0: return '#0066ff'; // Blue pill (trapped in restart cycle)
                case 1: return '#ff3333'; // Red pill (ByteHot freedom)
                case 2: 
                    // Transition effect - flickering between blue and red
                    const intensity = Math.sin(timer * 0.3) * 0.5 + 0.5;
                    return \`rgb(\${Math.floor(255 * intensity)}, \${Math.floor(100 * (1-intensity))}, \${Math.floor(255 * (1-intensity))})\`;
                default: return '#00ff00';
            }
        }
        
        function draw() {
            // Darker background for better contrast
            ctx.fillStyle = 'rgba(0, 0, 0, 0.08)';
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            
            ctx.font = '14px "Courier New", monospace';
            
            for(let i = 0; i < drops.length; i++) {
                // Awakening logic - blue pills can become red (people discovering ByteHot)
                if(dropTypes[i] === 0 && Math.random() < 0.001) { // 0.1% chance to start awakening
                    dropTypes[i] = 2; // Start awakening transition
                    awakeningTimer[i] = 0;
                }
                
                // Complete awakening after some time
                if(dropTypes[i] === 2) {
                    awakeningTimer[i]++;
                    if(awakeningTimer[i] > 100) { // After transition period
                        dropTypes[i] = 1; // Become red pill (awakened)
                        awakeningTimer[i] = 0;
                    }
                }
                
                // Occasionally reset red pills to blue (some fall back to old habits)
                if(dropTypes[i] === 1 && Math.random() < 0.0005) {
                    dropTypes[i] = 0;
                }
                
                const char = getCharacter(dropTypes[i]);
                const color = getColor(dropTypes[i], awakeningTimer[i]);
                
                ctx.fillStyle = color;
                
                // Add glow effect for red pills (ByteHot energy)
                if(dropTypes[i] === 1) {
                    ctx.shadowColor = '#ff3333';
                    ctx.shadowBlur = 8;
                } else if(dropTypes[i] === 2) {
                    // Pulsing glow during awakening
                    const glowIntensity = Math.sin(awakeningTimer[i] * 0.2) * 4 + 4;
                    ctx.shadowColor = '#ff6666';
                    ctx.shadowBlur = glowIntensity;
                } else {
                    ctx.shadowBlur = 0;
                }
                
                ctx.fillText(char, i * 15, drops[i] * 15);
                
                // Reset shadow
                ctx.shadowBlur = 0;
                
                // Drop physics - red pills fall slightly faster (momentum of change)
                const speed = dropTypes[i] === 1 ? 1.2 : 1.0;
                
                if(drops[i] * 15 > canvas.height && Math.random() > 0.975) { 
                    drops[i] = 0;
                    // New drops start as blue by default (most haven't discovered ByteHot yet)
                    if(Math.random() < 0.8) {
                        dropTypes[i] = 0;
                    }
                }
                drops[i] += speed;
            }
        }
        
        // Faster animation for more dynamic effect
        setInterval(draw, 40);
        
        // Handle window resize
        window.addEventListener('resize', () => { 
            canvas.width = window.innerWidth; 
            canvas.height = window.innerHeight;
            // Reinitialize drops for new width
            const newDropCount = Math.floor(canvas.width / 15);
            while(drops.length < newDropCount) {
                drops.push(1);
                dropColors.push(Math.random() < 0.7 ? 'blue' : 'red');
                dropTypes.push(dropColors[dropColors.length-1] === 'blue' ? 0 : 1);
                awakeningTimer.push(0);
            }
        });
        
        // Add a subtle hint about the metaphor on hover
        canvas.title = "🔴 Red: ByteHot (Freedom from restart tyranny) 🔵 Blue: Traditional dev (Restart slavery)";
EOF