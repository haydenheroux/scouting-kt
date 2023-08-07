<#-- @ftlvariable name="teams" type="kotlin.collections.List<io.github.haydenheroux.scouting.models.team.Team>" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <table border=1>
        <thead>
            <tr>
                <th>Number</th>
                <th>Name</th>
                <th>Region</th>
            </tr>
        </thead>
        <tbody>
        <#list teams as team>
        <tr>
            <td><a href="/teams/${team.teamData.number?c}">${team.teamData.number?c}</a></td>
            <td>${team.teamData.name}</td>
            <td>${team.teamData.region}</td>
        </tr>
        </#list>
        </tbody>
    </table>
</@layout.header>
