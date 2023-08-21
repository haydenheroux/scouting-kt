<#import "/common/links.ftl" as links />
<#import "/common/enums.ftl" as enums />

<#macro event_section event>
    <section>
        <h2><@links.event_link event=event /></h2>
        <p>${enums.region_to_text(event.region)}</p>
        <p>Week ${event.week?c}, ${event.year?c}</p>
        <#if event.matches?has_content>
            <#assign qualification_matches=event.matches?filter(match -> match.type == "QUALIFICATION")>
            <#if qualification_matches?has_content>
                <h3>Qualification Matches</h3>
                <@match_table event=event matches=qualification_matches />
            </#if>
            <#assign quarterfinal_matches=event.matches?filter(match -> match.type == "QUARTER_FINAL")>
            <#if quarterfinal_matches?has_content>
                <h3>Quarterfinal Matches</h3>
                <@match_table event=event matches=quarterfinal_matches />
            </#if>
            <#assign semifinal_matches=event.matches?filter(match -> match.type == "SEMI_FINAL")>
            <#if semifinal_matches?has_content>
                <h3>Semifinal Matches</h3>
                <@match_table event=event matches=semifinal_matches />
            </#if>
            <#assign final_matches=event.matches?filter(match -> match.type == "FINAL")>
            <#if final_matches?has_content>
                <h3>Final Matches</h3>
                <@match_table event=event matches=final_matches />
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
                <td><@links.match_link match=match event=event /></td>
                <#list match.alliances as alliance>
                    <#list alliance.participants as participant>
                        <td><@links.team_number_link team_number=participant.teamNumber /></td>
                    </#list>
                </#list>
            </tr>
        </#list>
        <tbody>
    </table>
</#macro>

<#macro season_section team season>
    <section>
    <h1><a href="/teams/${team.number?c}/${season.year?c}">${season.year?c}</a></h1>
    <hr/>
    <#list season.events as event>
    <@event_section event=event />
    </#list>
    </section>
</#macro>