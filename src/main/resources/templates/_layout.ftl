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

<#macro event_section event>
    <section>
        <h2>${event.name}</h2>
        <p>
        <@layout.rename_region region=event.region />
        </p>
        <p>Week ${event.week?c}, ${event.year?c}</p>
        <h3>Matches</h3>
        <table>
            <thead>
                <tr>
                    <th>Match</th>
                    <th colspan="6">Teams</th>
                </tr>
            </thead>
            <tbody>
            <#list event.matchReferences as matchReference>
                <tr>
                    <td>${matchReference.type[0]}${matchReference.number}</td>
                    <#list matchReference.participantReferences as participantReference>
                    <#local team_number=participantReference.robotReference.seasonReference.teamReference.number>
                    <td><a href="/teams/${team_number?c}/${event.year?c}">${team_number?c}</td>
                    </#list>
                </tr>
            </#list>
            <tbody>
        </table>
    </section>
    <hr/>
</#macro>

<#macro team_header team>
    <h1><a href="/teams/${team.number?c}">Team ${team.number?c} - ${team.name}</a></h1>
    <p><@layout.rename_region region=team.region /></p>
</#macro>

<#macro season_section season>
    <section>
    <h1><a href="/teams/${season.teamReference.number?c}/${season.year?c}">${season.year?c}</a></h1>
    <hr/>
    <#list season.eventReferences as eventReference>
    <@layout.event_section event=eventReference />
    </#list>
    </section>
</#macro>

<#macro rename_region region>
    <#if region == "NEW_ENGLAND">
    New England
    <#else>
    ${region}
    </#if>
</#macro>
