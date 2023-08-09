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
        <h2><a href="/events/${region_to_serial(event.region)}/${event.year?c}/${event.week?c}/${event.name}">${event.name}</a></h2>
        <p>${region_to_text(event.region)}</p>
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
            <#list event.matches as match>
                <tr>
                    <td><a href="/events/${region_to_serial(event.region)}/${event.year?c}/${event.week?c}/${event.name}/${match.number}">${match.type[0]}${match.number}</a></td>
                    <#list match.participants as participant>
                    <!--  TODO -->
                    <!-- <#local team_number=participant.robot.season.team.number> -->
                    <!-- <td><a href="/teams/${team_number?c}/${event.year?c}">${team_number?c}</td> -->
                    </#list>
                </tr>
            </#list>
            <tbody>
        </table>
    </section>
    <hr/>
</#macro>

<#macro team_link team size>
    <#if size == "large">
        <h1><a href="/teams/${team.number?c}">Team ${team.number?c} - ${team.name}</a></h1>
    <#elseif size == "small">
        <h2><a href="/teams/${team.number?c}">Team ${team.number?c} - ${team.name}</a></h2>
    <#else>
        <p><a href="/teams/${team.number?c}">Team ${team.number?c} - ${team.name}</a></p>
    </#if>
</#macro>

<#macro season_link season size>
    <#local team=season.teamReference>
    <#if size == "large">
        <h1><a href="/teams/${team.number?c}/${season.year?c}">Team ${team.number?c} - ${team.name}</a></h1>
    <#elseif size == "small">
        <h2><a href="/teams/${team.number?c}/${season.year?c}">Team ${team.number?c} - ${team.name}</a></h2>
    <#else>
        <p><a href="/teams/${team.number?c}/${season.year?c}">Team ${team.number?c} - ${team.name}</a></p>
    </#if>
</#macro>

<#macro season_section season>
    <section>
    <hr/>
    <#list season.events as event>
    <@layout.event_section event=event />
    </#list>
    </section>
</#macro>

<#function region_to_text region>
    <#if region == "NEW_ENGLAND">
        <#return "New England">
    <#else>
        <#return region>
    </#if>
</#function>

<#function region_to_serial region>
    <#if region == "NEW_ENGLAND">
        <#return "ne">
    <#else>
        <#return region>
    </#if>
</#function>

<#function match_type_to_text match_type>
    <#if match_type == "QUALIFICATION">
        <#return "Qualification">
    <#elseif match_type == "PLAYOFF">
        <#return "Playoff">
    <#else>
        <#return "Match">
    </#if>
</#function>


<#macro alliance alliance>
    <#if alliance == "RED">
        <p class="red pill">Red Alliance</p>
    <#elseif alliance == "BLUE">
        <p class="blue pill">Blue Alliance</p>
    <#else>
        <p class="pill">No Alliance</p>
    </#if>
</#macro>
