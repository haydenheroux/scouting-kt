<#-- @ftlvariable name="event" type="io.github.haydenheroux.scouting.models.event.Event" -->
<#-- @ftlvariable name="match" type="io.github.haydenheroux.scouting.models.match.Match" -->
<#import "/common/layout.ftl" as layout />
<#import "/common/links.ftl" as links />
<#import "/common/enums.ftl" as enums />
<#import "/common/match.ftl" as _match />
<@layout.header title="${_match.match_to_text_short(match)} - ${event.name}">
    <h1>${_match.match_to_text_short(match)} - <@links.event_link event=event /></h1>
    <hr/>
    <#list match.alliances as alliance>
        <section>
            <@enums.alliance alliance=alliance.color />
            <table>
                <thead>
                    <tr>
                        <th>Key</th>
                        <th>Value</th>
                    </tr>
                </thead>
                <tbody>
                <#list alliance.metrics?sort_by("key") as metric>
                    <tr>
                        <td>${metric.key}</td>
                        <td>${metric.value}</td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </section>
        <#list alliance.participants as participant>
            <section>
                <h2><@links.team_number_year_link team_number=participant.teamNumber year=event.year /></h2>
                <@enums.alliance alliance=alliance.color />
                <table>
                    <thead>
                        <tr>
                            <th>Key</th>
                            <th>Value</th>
                        </tr>
                    </thead>
                    <tbody>
                    <#list participant.metrics?sort_by("key") as metric>
                        <tr>
                            <td>${metric.key}</td>
                            <td>${metric.value}</td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </section>
            <hr/>
        </#list>
    </#list>
</@layout.header>
