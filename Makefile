
show:

vs:
	@rm -rf .bloop .metals project/.bloop/ .vscode

clean:
	@find . -name "target" | xargs rm -rf {} \;
