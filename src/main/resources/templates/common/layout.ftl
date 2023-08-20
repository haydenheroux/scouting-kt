<#macro header title>
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <title>${title}</title>
        <link rel="stylesheet" href="/static/main.css">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Mono:wght@100;200;300;400;500;600;700&family=Open+Sans:wght@300;400;500;600;700;800&family=Roboto:wght@100;400&family=Rubik:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">
    </head>
    <body>
        <header>
            <nav class="container">
                <a href="/">Home</a>
                <a href="/teams">Teams</a>
                <a href="/events">Events</a>
            </nav>
        </header>
        <main class="container">
            <#nested>
        </main>
    </body>
    </html>
</#macro>