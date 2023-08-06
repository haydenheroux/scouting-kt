<#macro header>
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <title>scouting-kt</title>
        <link rel="stylesheet" href="/static/main.css">
    </head>
    <body style="text-align: center; font-family: sans-serif">
    <#nested>
    </body>
    </html>
</#macro>

<#macro event_section event>
    <section>
        <h2>${event.name}</h2>
        <p>${event.region}</p>
        <p>Week ${event.week?c}, ${event.year?c}</p>
        <hr/>
        <h3>Matches</h3>
        <table>
            <thead>
            </thead>
            <tbody>
            <#list event.matches as match>
                <tr>
                    <td>${match.type} ${match.number}</td>
                    <#list match.metrics as metric>
                    <#local team_number=metric.robot.season.team.teamNumber>
                    <td><a href="/teams/${team_number?c}/${event.year?c}">${team_number?c}</td>
                    </#list>
                </tr>
            </#list>
            <tbody>
        </table>
    </section>
</#macro>
