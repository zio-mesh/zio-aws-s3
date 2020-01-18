
show:

vs:
	@rm -rf .bloop .metals project/.bloop/

clean:
	@find . -name "target" | xargs rm -rf {} \;
