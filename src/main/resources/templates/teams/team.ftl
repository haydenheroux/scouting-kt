<#-- @ftlvariable name="teamReference" type="io.github.haydenheroux.scouting.models.team.Team" -->
<#import "/common/_layout.ftl" as layout />
<@layout.header title="Team ${teamReference.number?c}">
    <@layout.team_header team=teamReference size="large" />
    <#list teamReference.seasonReferences?reverse as seasonReference>
        <@layout.season_section season=seasonReference />
    </#list>
</@layout.header>
