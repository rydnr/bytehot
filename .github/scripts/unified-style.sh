#!/usr/bin/env sh

cat <<EOF
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Courier New', monospace;
            background: linear-gradient(135deg, #0f0f23 0%, #1a1a3a 100%);
            color: #00ff00;
            line-height: 1.6;
            overflow-x: hidden;
        }

        .matrix-bg {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            pointer-events: none;
            z-index: -1;
            opacity: 0.05;
        }

        .nav-header {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            background: rgba(15, 15, 35, 0.95);
            backdrop-filter: blur(10px);
            padding: 1rem 2rem;
            z-index: 1000;
            border-bottom: 2px solid #00ff00;
        }

        .nav-links {
            display: flex;
            justify-content: center;
            gap: 1.5rem;
            flex-wrap: wrap;
        }

        .nav-link {
            color: #00ff00;
            text-decoration: none;
            padding: 0.4rem 0.8rem;
            border: 1px solid #00ff00;
            border-radius: 4px;
            transition: all 0.3s ease;
            font-weight: bold;
            font-size: 0.9rem;
        }

        .nav-link:hover {
            background: #00ff00;
            color: #0f0f23;
            box-shadow: 0 0 15px #00ff00;
            transform: translateY(-2px);
        }
        
        .nav-link.getting-started {
            background: linear-gradient(45deg, #ff6b00, #ff0080);
            border-color: #ff6b00;
            color: white;
            animation: highlight 3s ease-in-out infinite;
        }

        @keyframes highlight {
            0%, 100% { box-shadow: 0 0 10px #ff6b00; }
            50% { box-shadow: 0 0 30px #ff6b00, 0 0 40px #ff0080; }
        }

        .nav-link.getting-started:hover {
            background: linear-gradient(45deg, #ff8b20, #ff20a0);
            transform: translateY(-3px) scale(1.05);
        }

        .content {
            margin-top: 100px;
            padding: 2rem;
            max-width: 1200px;
            margin-left: auto;
            margin-right: auto;
        }

        .doc-container {
            background: rgba(26, 26, 58, 0.8);
            border: 1px solid #00ff00;
            border-radius: 12px;
            padding: 3rem;
            margin: 2rem 0;
        }

        .doc-container h1 {
            color: #00cccc;
            font-size: 2.5rem;
            margin: 0 0 2rem 0;
            text-shadow: 0 0 15px #00cccc;
            text-align: center;
        }

        .doc-container h2 {
            color: #00ff00;
            font-size: 1.8rem;
            margin: 2rem 0 1rem 0;
            border-left: 4px solid #00ff00;
            padding-left: 1rem;
            text-shadow: 0 0 10px #00ff00;
        }

        .doc-container h3 {
            color: #00cccc;
            font-size: 1.4rem;
            margin: 1.5rem 0 1rem 0;
        }

        .doc-container h4, .doc-container h5, .doc-container h6 {
            color: #ffffff;
            margin: 1rem 0 0.5rem 0;
        }

        .doc-container p {
            color: #ffffff;
            margin: 1rem 0;
            line-height: 1.8;
        }

        .doc-container ul, .doc-container ol {
            color: #ffffff;
            margin: 1rem 0;
            padding-left: 2rem;
        }

        .doc-container li {
            margin: 0.5rem 0;
            line-height: 1.6;
        }

        .doc-container code {
            background: rgba(0, 0, 0, 0.5);
            color: #00ff00;
            padding: 0.2rem 0.4rem;
            border-radius: 4px;
            font-family: 'Courier New', monospace;
            border: 1px solid rgba(0, 255, 0, 0.3);
        }

        .doc-container pre {
            background: rgba(0, 0, 0, 0.8);
            border: 1px solid #00ff00;
            border-radius: 8px;
            padding: 1.5rem;
            overflow-x: auto;
            margin: 1.5rem 0;
        }

        .doc-container pre code {
            background: none;
            padding: 0;
            border: none;
        }

        .doc-container a {
            color: #00cccc;
            text-decoration: none;
            border-bottom: 1px dotted #00cccc;
            transition: all 0.3s ease;
        }

        .doc-container a:hover {
            color: #ffffff;
            border-bottom-color: #ffffff;
            text-shadow: 0 0 5px #00cccc;
        }

        .doc-container table {
            width: 100%;
            border-collapse: collapse;
            margin: 1.5rem 0;
            background: rgba(0, 0, 0, 0.3);
        }

        .doc-container th, .doc-container td {
            border: 1px solid #00ff00;
            padding: 0.8rem;
            text-align: left;
        }

        .doc-container th {
            background: rgba(0, 255, 0, 0.1);
            color: #00ff00;
            font-weight: bold;
        }

        .doc-container td {
            color: #ffffff;
        }

        .doc-container blockquote {
            border-left: 4px solid #00cccc;
            padding-left: 1.5rem;
            margin: 1.5rem 0;
            color: #00cccc;
            font-style: italic;
            background: rgba(0, 204, 204, 0.05);
            padding: 1rem 1rem 1rem 1.5rem;
            border-radius: 0 8px 8px 0;
        }

        .footer {
            background: rgba(0, 0, 0, 0.8);
            padding: 2rem;
            text-align: center;
            border-top: 2px solid #00ff00;
            margin-top: 4rem;
        }

        .footer a {
            color: #00ff00;
            text-decoration: none;
        }

        .footer a:hover {
            text-shadow: 0 0 10px #00ff00;
        }

        .docs-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1.5rem;
            margin: 2rem 0;
        }

        .docs-link {
            display: block;
            color: #00cccc;
            text-decoration: none;
            padding: 1rem;
            border: 1px solid #00cccc;
            border-radius: 6px;
            transition: all 0.3s ease;
            text-align: center;
            font-weight: bold;
        }

        .docs-link:hover {
            background: rgba(0, 204, 204, 0.1);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 204, 204, 0.3);
        }

        .literate-section {
            background: rgba(0, 0, 0, 0.3);
            border-radius: 12px;
            padding: 2rem;
            margin: 2rem 0;
            border: 2px solid #00cccc;
        }

        .class-ref {
            background: rgba(0, 255, 0, 0.1);
            padding: 0.3rem 0.6rem;
            border-radius: 4px;
            border: 1px solid #00ff00;
            display: inline-block;
            margin: 0.2rem;
            color: #00ff00;
            text-decoration: none;
            font-weight: bold;
        }

        .class-ref:hover {
            background: rgba(0, 255, 0, 0.2);
            box-shadow: 0 0 10px #00ff00;
        }

        .revolutionary-banner {
            background: linear-gradient(45deg, #ff0080, #00ff00, #0080ff);
            background-size: 400% 400%;
            animation: gradient 4s ease infinite;
            padding: 2rem;
            text-align: center;
            margin: 2rem 0;
            position: relative;
            overflow: hidden;
            border-radius: 8px;
        }

        @keyframes gradient {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
        }

        .revolutionary-banner::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(15, 15, 35, 0.8);
        }

        .revolutionary-content {
            position: relative;
            z-index: 1;
        }

        .revolutionary-title {
            font-size: 1.8rem;
            margin-bottom: 1rem;
            color: #ffffff;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8);
        }

        @media (max-width: 768px) {
            .nav-links {
                gap: 0.8rem;
            }

            .nav-link {
                padding: 0.3rem 0.6rem;
                font-size: 0.8rem;
            }

            .content {
                padding: 1rem;
            }

            .doc-container {
                padding: 2rem;
            }

            .doc-container h1 {
                font-size: 2rem;
            }
        }
EOF
