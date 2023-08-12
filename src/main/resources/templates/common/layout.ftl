<#macro header title>
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <title>${title}</title>
        <link rel="stylesheet" href="/static/main.css">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,300;0,400;0,500;0,600;0,700;0,800;1,300;1,400;1,500;1,600;1,700;1,800&display=swap" rel="stylesheet">
    </head>
    <body>
        <header>
            <nav class="container">
                <ul>
                    <li class=""><a href="/">Home</a></li>
                    <li class=""><a href="/teams">Teams</a></li>
                    <li class=""><a href="/events">Events</a></li>
                </ul>
            </nav>
        </header>
        <main class="container">
            <#nested>
        </main>
    </body>
    </html>
</#macro>