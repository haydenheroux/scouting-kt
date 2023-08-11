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
        <#if event.matches?has_content>
            <#assign qualification_matches=event.matches?filter(match -> match.type == "QUALIFICATION")>
            <#if qualification_matches?has_content>
                <h3>Qualification Matches</h3>
                <@layout.match_table event=event matches=qualification_matches />
            </#if>
            <#assign quarterfinal_matches=event.matches?filter(match -> match.type == "QUARTER_FINAL")>
            <#if quarterfinal_matches?has_content>
                <h3>Quarterfinal Matches</h3>
                <@layout.match_table event=event matches=quarterfinal_matches />
            </#if>
            <#assign semifinal_matches=event.matches?filter(match -> match.type == "SEMI_FINAL")>
            <#if semifinal_matches?has_content>
                <h3>Semifinal Matches</h3>
                <@layout.match_table event=event matches=semifinal_matches />
            </#if>
            <#assign final_matches=event.matches?filter(match -> match.type == "FINAL")>
            <#if final_matches?has_content>
                <h3>Final Matches</h3>
                <@layout.match_table event=event matches=final_matches />
            </#if>
        </#if>
    </section>
    <hr/>
</#macro>

<#macro match_table event matches>
    <table>
        <thead>
            <tr>
                <th>Match</th>
                <th colspan="6">Teams</th>
            </tr>
        </thead>
        <tbody>
        <#if matches[0].type == "QUALIFICATION">
            <#local sorted_matches=matches?sort_by("number")>
        <#else>
            <#local sorted_matches=matches?sort_by("set")>
        </#if>
        <#list sorted_matches as match>
            <tr>
                <td><a href="/events/${region_to_serial(event.region)}/${event.year?c}/${event.week?c}/${event.name}/${match_to_serial(match)}">${match_to_text(match)}</a></td>
                <#list match.participants as participant>
                <td><a href="/teams/${participant.team.number?c}/${event.year?c}">${participant.team.number?c}</td>
                </#list>
            </tr>
        </#list>
        <tbody>
    </table>
</#macro>

<#macro team_link team>
    <a href="/teams/${team.number?c}">Team ${team.number?c} - ${team.name}</a>
</#macro>

<#macro team_number_link team>
    <a href="/teams/${team.number?c}">${team.number?c}</a>
</#macro>

<#macro team_year_link team year>
    <a href="/teams/${team.number?c}/${year?c}">Team ${team.number?c} - ${team.name}</a>
</#macro>

<#macro event_link event>
    <a href="/events/${region_to_serial(event.region)}/${event.year?c}/${event.week?c}/${event.name}">${event.name}</a>
</#macro>

<#macro season_section team season>
    <section>
    <h1><a href="/teams/${team.number?c}/${season.year?c}">${season.year?c}</a></h1>
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

<#function match_to_text match>
    <#return "${match_type_to_text(match.type)} ${set_to_text(match)} Match ${match.number?c}">
</#function>

<#function set_to_text match>
    <#if match.type != "QUALIFICATION">
        <#return "${match.set?c}">
    <#else>
        <#return "">
    </#if>
</#function>

<#function match_to_serial match>
    <#return "${match_type_to_serial(match.type)}${set_to_serial(match)}${match.number?c}">
</#function>

<#function set_to_serial match>
    <#if match.type != "QUALIFICATION">
        <#return "${match.set?c}m">
    <#else>
        <#return "">
    </#if>
</#function>

<#function match_type_to_text match_type>
    <#if match_type == "QUALIFICATION">
        <#return "Qualification">
    <#elseif match_type == "QUARTER_FINAL">
        <#return "Quarters">
    <#elseif match_type == "SEMI_FINAL">
        <#return "Semis">
    <#elseif match_type == "FINAL">
        <#return "Finals">
    <#else>
        <#return "Match">
    </#if>
</#function>

<#function match_type_to_serial match_type>
    <#if match_type == "QUALIFICATION">
        <#return "qm">
    <#elseif match_type == "QUARTER_FINAL">
        <#return "qf">
    <#elseif match_type == "SEMI_FINAL">
        <#return "sf">
    <#elseif match_type == "FINAL">
        <#return "f">
    <#else>
        <#return "">
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
