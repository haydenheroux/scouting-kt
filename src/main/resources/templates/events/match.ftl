<#-- @ftlvariable name="matchReference" type="io.github.haydenheroux.scouting.models.match.MatchReference" -->
<#import "/common/_layout.ftl" as layout />
<@layout.header title="${matchReference.type} ${matchReference.number} - ${matchReference.eventReference.name}">
    <h1>${matchReference.type} ${matchReference.number} - ${matchReference.eventReference.name}</h1>
    <hr/>
    <#list matchReference.participantReferences as participantReference>
        <section>
            <#assign teamReference=participantReference.robotReference.seasonReference.teamReference>
            <@layout.team_header team=teamReference />
            <@layout.alliance alliance=participantReference.alliance />
            <h3>Metrics</h3>
            <table>
                <thead>
                    <tr>
                        <th>Key</th>
                        <th>Value</th>
                    </tr>
                </thead>
                <tbody>
                <#list participantReference.metricReferences as metricReference>
                    <tr>
                        <td>${metricReference.key}</td>
                        <td>${metricReference.value}</td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </section>
        <hr/>
    </#list>
</@layout.header>
